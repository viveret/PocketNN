package com.viveret.pocketn2.view.fragments.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.fragments.data.nav.NavigationFragment
import com.viveret.tinydnn.data.nav.LocalFileInfo
import com.viveret.tinydnn.model.INetworkModelWithWeights
import com.viveret.tinydnn.network.SequentialNetworkModelWithWeights
import com.viveret.tinydnn.project.TemplateNetwork
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.nav.NavigationItem

/**
 * A fragment representing a list of Items.
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class ImportNNFragment : NavigationFragment() {
    private lateinit var listener: OnItemSelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnItemSelectedListener) {
            this.listener = context
        } else {
            throw Exception("Must implement OnItemSelectedListener")
        }
    }

    override fun onSelected(item: Any): OnSelectedResult {
        if (item is NavigationItem) {
            if (!item.file.isDirectory) {
                if (item.file.extension.toLowerCase() == "json") {
                    try {
                        return listener.onSelected(ImportTemplateNN(item))
                    } catch (e: Exception) {
                        Log.e("com.viveret.pocketn2", e.localizedMessage, e)
                        Toast.makeText(this.requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this.requireContext(), "Invalid file format (must be json)", Toast.LENGTH_LONG).show()
                    return OnSelectedResult(false)
                }
            }
        }
        return super.onSelected(item)
    }

    class ImportTemplateNN(val navItem: NavigationItem): TemplateNetwork {
        override fun apply(n: SequentialNetworkModelWithWeights) {
        }

        override val name: String = navItem.file.nameWithoutExtension

        override val description: String = ""

        override fun gen(name: String): INetworkModelWithWeights = SequentialNetworkModelWithWeights(name, (navItem.file as LocalFileInfo).f)
    }

    companion object {
        fun newInstance(title: Int, columnCount: Int): ImportNNFragment {
            val f = ImportNNFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE, title)
            f.arguments = args
            return f
        }
    }
}
