package com.viveret.pocketn2.view.fragments.data.packaging

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.databinding.FragmentChooseScenariosBinding
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.PackageGeneratorAdapter
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment
import com.viveret.pocketn2.view.holders.PackageGeneratorViewHolder
import com.viveret.tinydnn.data.transform.PackageGenerator
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [ChoosePackageGeneratorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChoosePackageGeneratorFragment : ProjectBottomSheetFragment<FragmentChooseScenariosBinding>(), OnItemSelectedListener {
    override fun onSelected(item: Any): OnSelectedResult = OnSelectedResult(dismissOnSelect)

    // override val fragmentRootViewResId = R.layout.fragment_choose_scenarios
    override val initialExpansionState: Int? = null
    private lateinit var myHelper: ChoosePackageGeneratorHelper
    var dismissOnSelect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && requireArguments().containsKey("dismissOnSelect")) {
            dismissOnSelect = requireArguments().getBoolean("dismissOnSelect")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)!!

        myHelper.onCreateView(binding!!.inputMethodList)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.myHelper = ChoosePackageGeneratorHelper(null, context, this.projectProvider!!.project!!)
        myHelper.onAttach(context)
    }

    override fun onDetach() {
        myHelper.onDetach()
        super.onDetach()
    }

    private class ChoosePackageGeneratorHelper(val dismissible: Dismissible?, context: Context, project: NeuralNetProject) : ListFragmentHelper<PackageGenerator, PackageGeneratorViewHolder, PackageGeneratorAdapter, OnItemSelectedListener>() {
        val values = DataManager.get(context).generators.filter { it.isMatch(project.get()) }

        override fun newAdapter(listener: OnItemSelectedListener?): PackageGeneratorAdapter =
                PackageGeneratorAdapter(values, dismissible, listener!!)

        override fun asValidListener(context: Context): OnItemSelectedListener? =
                if (context is OnItemSelectedListener) context else null

        override fun getListenerClassName(): String = "OnItemSelectedListener"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(): ChoosePackageGeneratorFragment =
                ChoosePackageGeneratorFragment()
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentChooseScenariosBinding? {
        return FragmentChooseScenariosBinding.inflate(inflater, container, false)
    }
}// Required empty public constructor
