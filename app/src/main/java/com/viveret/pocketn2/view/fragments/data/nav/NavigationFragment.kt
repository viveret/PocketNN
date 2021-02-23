package com.viveret.pocketn2.view.fragments.data.nav

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentNavigationListBinding
import com.viveret.pocketn2.view.HasTitle
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.NavigationAdapter
import com.viveret.pocketn2.view.holders.NavigationViewHolder
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.data.nav.FileNavigator
import com.viveret.tinydnn.data.nav.NavigationListener
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import com.viveret.tinydnn.util.nav.NavigationItem
import org.jetbrains.anko.runOnUiThread
import java.io.File


open class NavigationFragment : androidx.fragment.app.DialogFragment(), HasTitle, NavigationListener, OnItemSelectedListener {
    private lateinit var scrollView: HorizontalScrollView
    private lateinit var noItemsMsg: TextView
    private var start: String? = null
    private var inflatedView: View? = null
    private lateinit var titleBtns: LinearLayout
    protected open val navigator = FileNavigator()
    private lateinit var myHelper: NavigationHelper
    private var titleResId: Int? = null

    override val title: Int
        get() = titleResId!!

    override fun onItemsChange(items: Array<NavigationItem>) {
        myHelper.listView.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        noItemsMsg.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSelected(item: Any): OnSelectedResult {
        if (item is NavigationItem) {
            val f = item.file
                if (f.isDirectory) {
                    this.navigator.assign(item.file)
                } else {
                    return OnSelectedResult(true)
                }
        }
        return OnSelectedResult(false)
    }

    override fun onHistoryChange(history: Array<String>) {
    }

    override fun onLocationChange(location: File) {
        this.context?.runOnUiThread {
            val context = requireContext()
            val parts = location.canonicalPath.split("/")
            titleBtns.removeAllViews()
            for (i in parts.indices) {
                val completePath = parts.subList(parts.indices.first, i + 1).joinToString("/")// + "/"
                val btnView = CreateNavButton(context, completePath, i, parts)
                titleBtns.addView(btnView)
            }

            scrollView.postDelayed({ scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT) }, 10)
        }
    }

    private fun CreateNavButton(context: Context, completePath: String, i: Int, parts: List<String>): Button {
        val dir = File(completePath).absoluteFile
        Log.e("hi", dir.path)
        val btn = parts[i]
        val btnView = Button(context)
        btnView.text = btn
        btnView.tag = dir
        btnView.setOnClickListener { x -> onNavButtonClicked(x) }
        if (!dir.exists() || dir.listFiles() == null || !dir.canRead() || i == parts.indices.last) {
            btnView.isEnabled = false
        }
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        btnView.layoutParams = lp
        return btnView
    }

    private fun onNavButtonClicked(x: View) {
        try {
            navigator.assign(x.tag as File)
        } catch (e: Exception) {
            throw Exception("Could not list directory", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.addListener(this)
        myHelper = NavigationHelper(navigator)

        start = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).canonicalPath
        if (arguments != null) {
            this.onCreateArguments(requireArguments())
        }

        this.myHelper.onAttach(context)
        this.myHelper.onCreate(savedInstanceState)
    }

    private fun onCreateArguments(arguments: Bundle) {
        titleResId = arguments.getInt(ARG_TITLE)
        if (arguments.containsKey("start")) {
            val start = arguments.getString("start")!!
            this.start = start
        }
        myHelper.applyArguments(arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(requireActivity(), R.style.AppTheme)
        val binding = FragmentNavigationListBinding.inflate(inflater.cloneInContext(contextThemeWrapper), container, false)
        this.inflatedView = binding.root
        this.titleBtns = binding.titleBtns
        this.noItemsMsg = binding.tvNoItems
        this.scrollView = binding.scrollView
        myHelper.onCreateView(binding.list)
        navigator.addListener(myHelper.listView.adapter as NavigationAdapter)
        navigator.replace(File(this.start ?: ""))
        return this.inflatedView
    }

    /** The system calls this only when creating the layout in a dialog.  */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.title_navigator)
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PermissionsProvider) {
            context.requestReadAndWritePermissions {
                myHelper.onAttach(context)
            }
        }
    }

    override fun onDetach() {
        myHelper.onDetach()
        navigator.removeListener(this)
        super.onDetach()
    }

    private class NavigationHelper(val navigator: FileNavigator) : ListFragmentHelper<Vect, NavigationViewHolder, NavigationAdapter, OnItemSelectedListener>(1, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
        override fun newAdapter(listener: OnItemSelectedListener?): NavigationAdapter =
                NavigationAdapter(navigator, null, listener!!)

        override fun asValidListener(context: Context): OnItemSelectedListener? =
                context as OnItemSelectedListener

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    companion object {
        val ARG_TITLE = "title"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(title: Int, start: String?): NavigationFragment {
            val f = NavigationFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE, title)
            if (start != null) {
                args.putString("start", start)
            }
            f.arguments = args
            return f
        }
    }
}