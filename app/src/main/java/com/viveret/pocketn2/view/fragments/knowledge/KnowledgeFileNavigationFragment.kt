package com.viveret.pocketn2.view.fragments.knowledge

import android.os.Bundle
import com.viveret.pocketn2.view.fragments.data.nav.NavigationFragment
import com.viveret.tinydnn.data.nav.FileNavigator
import com.viveret.tinydnn.data.nav.FilteredFileNavigator
import com.viveret.tinydnn.data.nav.GenericFileInfo
import com.viveret.tinydnn.util.nav.NavigationItem
import java.io.File
import java.util.*

class KnowledgeFileNavigationFragment: NavigationFragment() {
    private fun resolveVirtualPath(path: String): Array<NavigationItem> {
        val pathRelative = path.substring(PATH_SELECT_SOURCE.length)
        if (pathRelative.isEmpty()) {
            return arrayOf(NavigationItem(GenericFileInfo("From Catalog", Date().time, true)))
        } else {
            return arrayOf(NavigationItem(GenericFileInfo("mckeeman.html", Date().time, false)))
        }
    }

    override val navigator: FileNavigator = FilteredFileNavigator { f -> f.file.extension == ".html"}

    init {
        navigator.addVirtualPath(PATH_SELECT_SOURCE) { p -> this.resolveVirtualPath(p) }
    }

    override fun onLocationChange(location: File) {
        if (location.canonicalPath == PATH_SELECT_SOURCE) {

        } else {
            super.onLocationChange(location)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(): KnowledgeFileNavigationFragment {
            val f = KnowledgeFileNavigationFragment()
                val args = Bundle()
                args.putString("start", PATH_SELECT_SOURCE)
                f.arguments = args
            return f
        }

        const val PATH_SELECT_SOURCE = "//select-source/"
    }
}