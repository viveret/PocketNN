package com.viveret.pocketn2.view.fragments.data

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentDataViewerBinding
import com.viveret.pocketn2.view.fragments.data.fmt.*
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult

open class DataViewerFragment : androidx.fragment.app.DialogFragment(), OnItemSelectedListener, Dismissible {
    override fun onSelected(item: Any): OnSelectedResult {
        return OnSelectedResult(false) // todo: implement
    }

    protected var _binding: FragmentDataViewerBinding? = null
    protected var inflatedView: View? = null
    private var viewToUse: Int? = null
    val possibleViews = listOf(ToStringView(), MovieCategoryView(), DistributionView(), IdxImageView(), BinaryGateView(), CifarImageView())
    val viewForData
            get() = possibleViews.single { x -> x.nameResId == viewToUse }
    protected val dm
            get() = DataManager.get(requireContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            this.onCreateArguments(requireArguments())
        }
    }

    open fun onCreateArguments(arguments: Bundle) {
        if (arguments.containsKey("viewId")) {
            this.viewToUse = arguments.getInt("viewId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), R.style.AppTheme)
        val binding = FragmentDataViewerBinding.inflate(inflater.cloneInContext(contextThemeWrapper), container, false)
        _binding = binding
        this.inflatedView = binding.root
        return this.inflatedView
    }

    /** The system calls this only when creating the layout in a dialog.  */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.title_data_viewer)
        return dialog
    }
}