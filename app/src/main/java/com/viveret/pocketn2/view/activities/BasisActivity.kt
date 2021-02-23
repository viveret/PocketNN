package com.viveret.pocketn2.view.activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.viveret.pocketn2.MainActivity
import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.adapters.MinimizableAdapter
import com.viveret.pocketn2.view.fragments.sandbox.SandboxFragment
import com.viveret.pocketn2.view.model.DismissReason
import com.viveret.pocketn2.view.model.MinimizableDialog
import com.viveret.tinydnn.util.AppLifecycleContext
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.min


abstract class BasisActivity : AppCompatActivity(), OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, PermissionsProvider {
    val appLifecycleContext = AppLifecycleContext(this)
    private val requestPermissionCallbacks = ArrayList<(Context) -> Unit>()
    protected var lastFragment: Fragment? = null
    private var isSwitchingMode = false
    private val delayedSwitchMode = ArrayList<Pair<String, (Bundle) -> Unit>>()
    private val minimizedDialogs = ArrayList<MinimizableDialog>()
    private lateinit var minimizedDialogsAdapter: MinimizableAdapter

    protected abstract fun getDrawerLayout(): DrawerLayout// = findViewById<DrawerLayout>(R.id.drawer_layout)
    protected abstract fun getToolbar(): androidx.appcompat.widget.Toolbar// = findViewById<DrawerLayout>(R.id.drawer_layout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CreateBinder())
        setSupportActionBar(getToolbar())

        val drawer = getDrawerLayout()
        val toggle = ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        minimizedDialogsAdapter = MinimizableAdapter(minimizedDialogs)

        val navItem = navigationView.menu.findItem(R.id.nav_1234)
        if (navItem != null) {
            val actionView = navItem.actionView
            val minimizedDialogsView = actionView as RecyclerView

            val mgr = LinearLayoutManager(this)
            mgr.orientation = RecyclerView.VERTICAL
            minimizedDialogsView.layoutManager = mgr
            minimizedDialogsView.adapter = minimizedDialogsAdapter
        }

        appLifecycleContext.onCreate()
    }

    abstract fun CreateBinder(): View

    fun switchToFragment(frag: Fragment) {
        isSwitchingMode = true
        val lastFragment = lastFragment
        if (lastFragment != null) {
            if (lastFragment.isAdded) {
                removeLastFragment()
            }
            this.lastFragment = null
        }

        if (frag is androidx.fragment.app.DialogFragment) {
            frag.show(supportFragmentManager, null)
        } else {
            supportFragmentManager.beginTransaction().add(R.id.fragment, frag).addToBackStack(null).commit()
        }

        if (frag !is SandboxFragment) {
            this.lastFragment = frag
        }
    }

    protected fun removeLastFragment() {
        if (lastFragment is androidx.fragment.app.DialogFragment) {
            (lastFragment as androidx.fragment.app.DialogFragment).dismiss()
            this.lastFragment = null
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        }
    }

    override fun onBackPressed() {
        val drawer = getDrawerLayout()
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            supportFragmentManager.backStackEntryCount > 1 -> supportFragmentManager.popBackStackImmediate()
            else -> finish()
        }
    }

    override fun onSelected(item: Any): OnSelectedResult {
        return if (item is MinimizableDialog) {
            if (item.dismissReason == DismissReason.Minimize && !item.visible) {
                minimizedDialogs.add(item)
            } else {
                minimizedDialogs.remove(item)
            }
            runOnUiThread {
                minimizedDialogsAdapter.notifyDataSetChanged()
            }
            OnSelectedResult(true)
        } else {
            OnSelectedResult(false)
        }
    }

    protected fun retry(titleStringId: Int, messageStringId: Int, otherOptions: Array<Int>, onItemSelected: (item: Int) -> Unit) {
        runOnUiThread {
            val dialog = AlertDialog.Builder(this).apply {
                setCancelable(false)
                setMessage(getString(messageStringId))
                setTitle(getString(titleStringId))
            }.create()
            dialog.setCanceledOnTouchOutside(false)

            val buttons = arrayOf(AlertDialog.BUTTON1, AlertDialog.BUTTON2, AlertDialog.BUTTON3)
            for (i in 0 until min(otherOptions.size, buttons.size)) {
                // TODO: Ensure this shit doesn't fuck up
                dialog.setButton(buttons[i], getString(otherOptions[i])) { _, _ -> onItemSelected(otherOptions[i]) }
            }

            dialog.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_READ_AND_WRITE)
    fun requestReadAndWritePermissions() {
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            for (requestPermissionCallback in this.requestPermissionCallbacks) {
                requestPermissionCallback(this)
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "getString(com.viveret.pocketn2.R.string.camera_and_location_rationale)",
                    RC_READ_AND_WRITE, *perms)
        }
    }

    override fun requestReadAndWritePermissions(callback: (Context) -> Unit) {
        this.requestPermissionCallbacks.add(callback)
        requestReadAndWritePermissions()
    }

    @AfterPermissionGranted(RC_INTERNET)
    fun requestInternetPermissions() {
        val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            for (requestPermissionCallback in this.requestPermissionCallbacks) {
                requestPermissionCallback(this)
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "getString(com.viveret.pocketn2.R.string.internet_rationale)",
                    RC_INTERNET, *perms)
        }
    }

    override fun requestInternetPermissions(callback: (Context) -> Unit) {
        this.requestPermissionCallbacks.add(callback)
        requestInternetPermissions()
    }

    override fun onResume() {
        super.onResume()
        appLifecycleContext.onResume()
    }

    override fun onPause() {
        appLifecycleContext.onPause()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        appLifecycleContext.onStart()
    }

    override fun onStop() {
        appLifecycleContext.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        appLifecycleContext.onDestroy()
        super.onDestroy()
    }

    fun showFragment(frag: Fragment) {
        if (frag is BottomSheetDialogFragment) {
            frag.showNow(supportFragmentManager, null)
        } else {
            supportFragmentManager.beginTransaction().add(R.id.fragment, frag).addToBackStack(null).commit()
        }
    }

    override fun finish() {
        AlertDialog.Builder(this)
                .setTitle(R.string.title_leaving)
                .setMessage(R.string.msg_leaving)
                .setPositiveButton(R.string.action_home) { _: DialogInterface, _: Int -> this@BasisActivity.goHome() }
                .setNegativeButton(R.string.action_quit) { _: DialogInterface, _: Int -> super@BasisActivity.finish() }
                .create().show()
    }

    protected fun goHome() {
        val home = Intent(this, MainActivity::class.java)
        startActivity(home)
        super.finish()
    }

    companion object {
        const val RC_READ_AND_WRITE = 2123
        const val RC_INTERNET = 2124
    }
}