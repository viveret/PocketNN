package com.viveret.pocketn2.view.fragments.sandbox

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.databinding.FragmentChooseScenariosBinding
import com.viveret.pocketn2.view.ListFragmentHelper
import com.viveret.pocketn2.view.adapters.ScenarioAdapter
import com.viveret.pocketn2.view.fragments.project.ProjectBottomSheetFragment
import com.viveret.pocketn2.view.holders.ScenarioViewHolder
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.train.TrainingMethod
import com.viveret.tinydnn.util.async.Dismissible
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProjectController] interface
 * to handle interaction events.
 * Use the [ChooseScenarioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseScenarioFragment : ProjectBottomSheetFragment<FragmentChooseScenariosBinding>(), OnItemSelectedListener {
    override fun onSelected(item: Any): OnSelectedResult {
        return OnSelectedResult(dismissOnSelect)
    }

    override val initialExpansionState: Int? = null
    private val myHelper = ChooseScenarioHelper(null)
    private lateinit var dataMethod: DataMethod
    var dismissOnSelect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && requireArguments().containsKey("item")) {
            this.dataMethod = DataMethod.valueOf(requireArguments().getString("item")!!)
            myHelper.values.addAll(DataManager.get(this.requireContext()).getScenariosForDataMethod(this.dataMethod)
                    .filter { x -> x.compatibleWithNetwork(this.projectProvider!!.project!!) })
        } else {
            throw IllegalArgumentException("Must define item for ChooseScenarioFragment")
        }

        if (requireArguments().containsKey("dismissOnSelect")) {
            dismissOnSelect = requireArguments().getBoolean("dismissOnSelect")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myHelper.onAttach(context)
    }

    override fun onDetach() {
        myHelper.onDetach()
        super.onDetach()
    }

    private class ChooseScenarioHelper(val dismissible: Dismissible?) : ListFragmentHelper<Scenario, ScenarioViewHolder, ScenarioAdapter, OnItemSelectedListener>() {
        val values = ArrayList<Scenario>()

        override fun newAdapter(listener: OnItemSelectedListener?): ScenarioAdapter =
                ScenarioAdapter(values, dismissible, listener!!)

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
        fun newInstance(config: TrainingMethod): ChooseScenarioFragment {
            val fragment = ChooseScenarioFragment()

            val args = Bundle()
            args.putString("item", config.dataMethod.name)
            fragment.arguments = args

            return fragment
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SandboxFragment.
         */
        fun newInstance(dataMethod: DataMethod): ChooseScenarioFragment {
            val fragment = ChooseScenarioFragment()

            val args = Bundle()
            args.putString("item", dataMethod.name)
            fragment.arguments = args

            return fragment
        }
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentChooseScenariosBinding? {
        val binding = FragmentChooseScenariosBinding.inflate(inflater, container, false)

        myHelper.onCreateView(binding.inputMethodList)

        return binding
    }
}// Required empty public constructor
