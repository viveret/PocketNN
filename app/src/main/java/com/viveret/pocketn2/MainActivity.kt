package com.viveret.pocketn2

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.viveret.pocketn2.async.MenuItemSelectedListener
import com.viveret.pocketn2.databinding.ActivityMainBinding
import com.viveret.pocketn2.view.HasTitle
import com.viveret.pocketn2.view.activities.ChallengeActivity
import com.viveret.pocketn2.view.activities.SandboxActivity
import com.viveret.pocketn2.view.dialog.WhatsNewDialog
import com.viveret.pocketn2.view.fragments.challenge.ChallengeListFragment
import com.viveret.pocketn2.view.fragments.main.*
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.project.TemplateNetwork
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult


class MainActivity : AppCompatActivity(), OnItemSelectedListener, MenuItemSelectedListener, LearnMoreFragment.OnLearnMoreSectionSelectedListener {
    private var activityTitle: CharSequence? = null
    private lateinit var currentFragment: androidx.fragment.app.Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, MainActivityFragment(), "main").commit()

        val whatsNewDialog = WhatsNewDialog(this)
        if (whatsNewDialog.shouldBeShown) {
            whatsNewDialog.show()
        }

        setSupportActionBar(binding.toolbar)

        activityTitle = title
        supportFragmentManager.addOnBackStackChangedListener {
            val frag = supportFragmentManager.fragments.last()
            if (frag is HasTitle) {
                setTitle(frag.title)
            } else {
                title = activityTitle
            }
        }
//        doAsync {
//            val s = ServerSocket(7777)
//            val client = s.accept()
//            client.use {
//                val os = it.getOutputStream()
//                os.write("<html><body>Hello from pocket n2</body></html>".toByteArray())
//                os.flush()
//            }
//        }
    }

    override fun onSelected(item: Any): OnSelectedResult {
        return when {
            item is TemplateNetwork -> {
                val txtName = EditText(this)
                txtName.hint = item.name
                AlertDialog.Builder(this)
                        .setTitle(R.string.new_layer_name_title)
                        .setMessage(R.string.new_layer_name_subtitle)
                        .setView(txtName)
                        .setPositiveButton(R.string.create_lbl) { _, _ ->
                            var newName = txtName.text.toString()
                            if (newName.isEmpty()) {
                                newName = item.name
                            }

                            val i = Intent(this@MainActivity, SandboxActivity::class.java)
                            i.putExtra(SandboxActivity.NETWORK_HANDLE, item.gen(newName).nativeObjectHandle)
                            startActivity(i)
                            finish()
                        }.show()
                OnSelectedResult(true)
            }
            item is ChallengeMetaInfo -> {
                val i = Intent(this@MainActivity, ChallengeActivity::class.java)
                i.putExtra(ChallengeActivity.ARG_CHALLENGE_ID, item.id.toString())
                startActivity(i)
                finish()
                OnSelectedResult(true)
            }
            currentFragment is OnItemSelectedListener -> {
                (currentFragment as OnItemSelectedListener).onSelected(item)
            }
            else -> {
                OnSelectedResult(false)
            }
        }
    }

    override fun onActionSelected(id: Int): Boolean {
        val frag: androidx.fragment.app.Fragment
        when (id) {
            R.id.action_from_scratch -> {
                val i = Intent(this, SandboxActivity::class.java)
                i.putExtra(SandboxActivity.INITIAL_MODE, SandboxActivity.MODE_ADD_LAYER)
                startActivity(i)
                finish()
                return true
            }
            R.id.action_from_template -> frag = TemplateNNFragment.newInstance(2)
            R.id.action_from_file -> frag = ImportNNFragment.newInstance(R.string.title_from_file, 1)
            R.id.action_challenge_mode -> frag = ChallengeListFragment.newInstance(1)
            R.id.btnAboutApp -> {
                val moreAboutApp = getString(R.string.learn_more_about_app)
                frag = LearnMoreFragment.newInstance(moreAboutApp, R.string.title_learn_more_about_app)
            }
            R.id.btnAboutML -> {
                val aboutMl = getString(R.string.learn_more_about_ml)
                frag = LearnMoreFragment.newInstance(aboutMl, R.string.title_learn_more_about_ml)
            }
            R.id.btnSettings -> frag = PrefsFragment.newInstance()
            R.id.btnContribute -> {
                val uri = Uri.parse(getString(R.string.contribute_url))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                return true
            }
            else -> return false
        }

        this.currentFragment = frag
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment, frag)
        ft.addToBackStack("from-template")
        ft.commit()
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

    override fun onLearnMoreSectionSelected(itemId: String) {
        when (itemId) {
            "libraries" -> showMessage(R.string.title_libraries_used, R.string.content_libraries_used).show()
            "licenses" -> showMessage(R.string.title_licenses, R.string.content_licenses).show()
            "contact" -> {
                val adb = showMessage(R.string.title_contact, R.string.content_contact)
                adb.setNeutralButton(R.string.help_request_button_lbl) { dialog, _ ->
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "plain/text"
                    i.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_request_subject))
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.help_request_starter_body))
                    this@MainActivity.startActivity(Intent.createChooser(i, getString(R.string.help_request_intent_lbl)))
                    dialog.dismiss()
                }
                adb.show()
            }
            "layer_types" -> openLink("https://en.wikipedia.org/wiki/Types_of_artificial_neural_networks")
            "tiny_dnn" -> openLink("https://github.com/tiny-dnn/tiny-dnn")
            "basics" -> openLink("http://jalammar.github.io/visual-interactive-guide-basics-neural-networks/")
            "neural_networks" -> openLink("http://neuralnetworksanddeeplearning.com/chap1.html")
            "neural_layers" -> openLink("https://towardsdatascience.com/multi-layer-neural-networks-with-sigmoid-function-deep-learning-for-rookies-2-bf464f09eb7f")
            "neurons" -> openLink("https://ml.berkeley.edu/blog/2017/02/04/tutorial-3/")
        }
    }

    private fun openLink(url: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    private fun showMessage(titleId: Int, messageId: Int): AlertDialog.Builder =
            showMessage(getString(titleId), getString(messageId))

    private fun showMessage(title: String, message: String): AlertDialog.Builder {
        return AlertDialog.Builder(this)
                .setTitle(title)
                .setPositiveButton("Close") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setMessage(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY) else Html.fromHtml(message))
    }

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}