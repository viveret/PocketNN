package com.viveret.pocketn2.view.fragments.scenario

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentScenarioImportBinding
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.NavigationAdapter
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.pocketn2.view.holders.NavigationViewHolder
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.io.InputSelection
import com.viveret.tinydnn.data.nav.FileNavigator
import com.viveret.tinydnn.data.nav.NavigationListener
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult
import com.viveret.tinydnn.util.async.PermissionsProvider
import com.viveret.tinydnn.util.nav.NavigationItem
import org.jetbrains.anko.runOnUiThread
import java.io.File
import java.util.*

class ImportScenarioFragment : ScenarioFragment<FragmentScenarioImportBinding>(), NavigationListener, OnItemSelectedListener {
    override val dismissAfterSubmit: Boolean = true
    private lateinit var start: String
    protected val navigator = FileNavigator()
    private lateinit var myHelper: NavigationHelper
    override val initialExpansionState: Int? = BottomSheetBehavior.STATE_EXPANDED
    private val inputs = InputSelection()
    private val rolesLeft = mutableListOf(R.string.role_input to DataRole.Input,
            R.string.role_input_labels to DataRole.InputLabels,
            R.string.role_fit_to to DataRole.FitTo,
            R.string.role_fit_to_labels to DataRole.FitToLabels)


    override fun onSelected(item: Any): OnSelectedResult {
        return if (item is InputSelection) {
            //submitData()
            OnSelectedResult(true)
        } else if (item is NavigationItem) {
            val f = item.file
            when {
                f.isDirectory -> this.navigator.onSelected(item)
                //this.navigator.assign(item.file.canonicalPath)
                rolesLeft.isNotEmpty() -> {
                    val hash = f.canonicalPath.hashCode()
                    val file = File(f.canonicalPath)
                    val role = rolesLeft.removeAt(0).second
                    inputs[role] = InputSelection.FileItem(FileStream(requireContext(), file, UUID(hash.toLong(), hash.toLong()), role), DataSource.LocalFile)

                    binding!!.fragmentSubtitle.setText(rolesLeft.first().first)
                    binding!!.fragmentSubtitle.text = "Select ${binding!!.fragmentSubtitle.text} (or tap to skip)"
                }
                else -> submit()
            }
            OnSelectedResult(true)
        } else {
            OnSelectedResult(false)
            //throw java.lang.Exception("Invalid selection: $item")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).canonicalPath
        this.myHelper.onAttach(context)
        this.myHelper.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PermissionsProvider) {
            context.requestReadAndWritePermissions {
                myHelper = NavigationHelper(navigator, this)
                navigator.addListener(this)
                myHelper.onAttach(context)
            }
        }
    }

    override fun onDetach() {
        myHelper.onDetach()
        navigator.removeListener(myHelper.listView.adapter as NavigationAdapter)
        navigator.removeListener(this)
        super.onDetach()
    }

    override fun onHistoryChange(history: Array<String>) { }

    override fun onItemsChange(items: Array<NavigationItem>) { }

    override fun onLocationChange(location: File) {
        this.context?.runOnUiThread {
            val btns = location.canonicalPath.split("/")
            //_binding!!.b.removeAllViews()
            for (i in btns.indices) {
                val btn = btns[i]
                val completePath = btns.subList(0, i + 1).joinToString("/") + "/"
                val btnView = Button(requireContext())
                btnView.text = btn + "/"
                btnView.tag = File(completePath)
                btnView.setOnClickListener { x -> navigator.assign(x.tag as File) }
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                btnView.layoutParams = lp
                //titleBtns.addView(btnView)
            }
        }
    }

    private class NavigationHelper(val navigator: FileNavigator, val primaryListener: OnItemSelectedListener) : ListFragmentHelper<Vect, NavigationViewHolder, NavigationAdapter, OnItemSelectedListener>(1, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL) {
        override fun newAdapter(listener: OnItemSelectedListener?): NavigationAdapter =
                NavigationAdapter(primaryListener, null, primaryListener) // listener!!

        override fun asValidListener(context: Context): OnItemSelectedListener? =
                context as OnItemSelectedListener

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    override fun submit(): OnSelectedResult {
        return try {
            val roles = if (this.fitToOutput)
                arrayOf(DataRole.Input, DataRole.InputLabels, DataRole.FitTo, DataRole.FitToLabels)
            else
                arrayOf(DataRole.Input, DataRole.InputLabels)


//            fmt.open(files,
//                    if (fitTo)
//                        arrayOf(DataRole.Input, DataRole.InputLabels, DataRole.FitTo, DataRole.FitToLabels)
//                    else
//                        arrayOf(DataRole.Input, DataRole.InputLabels)
//            )
            // , this.dataFormat.open(inputs, roles)
            val stream = InputDataValueStream(requireContext(), projectProvider!!.project!!, inputs)
            this.submitData(stream)
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.fragmentTitle)
            OnSelectedResult(false)
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioImportBinding? {
        val binding = FragmentScenarioImportBinding.inflate(inflater, container, false)

        myHelper.onCreateView(binding.fragmentNavigationList.list)
        navigator.addListener(myHelper.listView.adapter as NavigationAdapter)
        navigator.replace(File(this.start))

        binding.fragmentTitle.text = scenario.name

        binding.fragmentSubtitle.setText(rolesLeft.first().first)
        binding.fragmentSubtitle.text = "Select ${binding.fragmentSubtitle.text}"

        binding.fragmentSubtitle.setOnClickListener {
            if (rolesLeft.size > 1) {
                rolesLeft.removeAt(0)
                binding.fragmentSubtitle.setText(rolesLeft.first().first)
                binding.fragmentSubtitle.text = "Select ${binding.fragmentSubtitle.text}"
            } else {
                submit()
            }
        }

        return binding
    }
}