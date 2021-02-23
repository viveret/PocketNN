package com.viveret.pocketn2.view.fragments.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentSandboxAddLayerBinding
import com.viveret.pocketn2.view.activities.ChallengeActivity
import com.viveret.pocketn2.view.holders.InputViewHolder
import com.viveret.pocketn2.view.widget.InputViewBuilder
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.error.NNException
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.layer.LayerBase
import com.viveret.tinydnn.network.SequentialNetworkModelWithWeights
import com.viveret.tinydnn.project.actions.AddLayerAction
import com.viveret.tinydnn.project.actions.InsertLayerAction
import com.viveret.tinydnn.reflection.annotations.UserConstructor
import com.viveret.tinydnn.reflection.annotations.UserField
import com.viveret.tinydnn.reflection.annotations.UserFields
import com.viveret.tinydnn.util.LayerId2Type
import com.viveret.tinydnn.util.StringIdMap
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.reflect.Constructor
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddLayerFragment.OnLayerAddedListener] interface
 * to handle interaction events.
 * Use the [AddLayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddLayerFragment : ProjectBottomSheetFragment<FragmentSandboxAddLayerBinding>(), OnItemSelectedListener {
    private fun challenge(): ChallengeMetaInfo? {
        val activity = requireActivity()
        return if (activity is ChallengeActivity) activity.challenge else null
    }

    private lateinit var inputCollector: InputViewHolder
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button
    private lateinit var pageNumberTv: TextView
    private lateinit var allConstructors: List<Pair<Constructor<*>, UserConstructor>>
    private lateinit var layerConstructors: List<Pair<Constructor<*>, UserConstructor>>
    override val initialExpansionState: Int? = null

    private lateinit var layerTypes: StringIdMap
    private lateinit var layerTypeSelector: Spinner
    private var tableView: LinearLayout? = null
    private var insertLayerPosition: Long = -1
    private var pageNumber = 0

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.pageNumber = 0
        this.layerConstructors = this.allConstructors.filter { x -> x.second.name == this.layerTypes.ids[position] }.sortedBy { x -> x.first.parameterTypes.size }
        invalidateConstructorView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
//            if (requireArguments().containsKey("challenge_id")) {
//                this.challenge = DataManager.get(requireContext()).getChallengeMetaInfo(UUID.fromString(requireArguments().getString("challenge_id")))
//            }
        }

        this.insertLayerPosition = arguments?.getLong(KEY_INSERT_POSITION) ?: -1

        val allConstructors = ArrayList<Pair<Constructor<*>, UserConstructor>>()
        for (layerType in LayerId2Type.LAYER_TYPES.values) {
            for (layerConstructor in layerType.constructors) {
                val userConstructor = layerConstructor.annotations.filterIsInstance(UserConstructor::class.java).singleOrNull()
                if (userConstructor != null) {
                    allConstructors.add(Pair(layerConstructor, userConstructor))
                }
            }
        }

        if (projectProvider?.project?.get()?.layer_size() == 0L || insertLayerPosition == 0L) {
            // First layer
            if (challenge() != null) {
                val constructorsWithInDim = ArrayList<Pair<Constructor<*>, UserConstructor>>()
                for (layerConstructor in allConstructors) {
                    val paramAnnotations = layerConstructor.first.parameterAnnotations.flatMap { pa -> pa.filterIsInstance(UserField::class.java) }.map { x -> x.type }
                    if (paramAnnotations.contains(UserFields.InDim) || (paramAnnotations.contains(UserFields.InWidth) && paramAnnotations.contains(UserFields.InHeight))) {
                        constructorsWithInDim.add(layerConstructor)
                    }
                }
                this.allConstructors = constructorsWithInDim
            } else {
                val constructorsWithoutPrevLayer = ArrayList<Pair<Constructor<*>, UserConstructor>>()
                for (layerConstructor in allConstructors) {
                    val paramAnnotations = layerConstructor.first.parameterAnnotations.flatMap { pa -> pa.filterIsInstance(UserField::class.java) }.map { x -> x.type }
                    if (!paramAnnotations.contains(UserFields.PreviousLayer)) {
                        constructorsWithoutPrevLayer.add(layerConstructor)
                    }
                }
                this.allConstructors = constructorsWithoutPrevLayer
            }
        } else {
            this.allConstructors = allConstructors
        }

        layerTypes = StringIdMap(this.allConstructors.map { it.second.name }.distinct().toTypedArray())
        layerTypes.attach(requireContext())
    }

    private fun invalidateConstructorView() {
        this.errorContainer.hide()
        this.pageNumberTv.text = "${pageNumber + 1} of ${this.layerConstructors.size}"
        val ltc = layerConstructors[pageNumber]
        tableView?.removeAllViews()

        if (projectProvider != null) {
            val nn = projectProvider?.project?.get()
            if (nn != null && nn is SequentialNetworkModelWithWeights) {
                val prevLayer = if (nn.size() > 0 && this.insertLayerPosition > 0) nn.layerAt(this.insertLayerPosition - 1) as LayerBase else null
                val nextLayer = if (nn.size() > 0 && this.insertLayerPosition < nn.size() - 1) nn.layerAt(this.insertLayerPosition + 1) as LayerBase else null

                this.inputCollector = InputViewBuilder(layoutInflater, tableView!!, ltc.first, prevLayer, nextLayer, challenge()).build()
            }
        }
    }

    private fun previousConstructor() {
        pageNumber--
        if (pageNumber < 0) {
            pageNumber = this.layerConstructors.size - 1
        }
        invalidateConstructorView()
    }

    private fun nextConstructor() {
        pageNumber++
        if (pageNumber >= this.layerConstructors.size) {
            pageNumber = 0
        }
        invalidateConstructorView()
    }

    private fun onLayerConfirmed() {
        try {
            if (projectProvider != null) {
                val nn = projectProvider?.project?.get()
                if (nn != null && nn is SequentialNetworkModelWithWeights) {
                    this.onLayerConfirmed(nn)
                } else {
                    throw NNException("Network must be sequential")
                }
            } else {
                throw UserException("Cannot add new layer without existing network")
            }
        } catch (e: Exception) {
            errorContainer.setError(e)
        }
    }

    private fun onLayerConfirmed(nn: SequentialNetworkModelWithWeights) {
        doAsync {
            try {
                val newLayer: LayerBase
                try {
                    newLayer = getLayerFromOptions()
                }  catch (e: UserException){
                    errorContainer.setError(e)
                    return@doAsync
                }

                try {
                    if (insertLayerPosition >= 0 && insertLayerPosition < nn.size()) {
                        nn.insertLayer(insertLayerPosition, newLayer)
                        projectProvider?.project?.notifyObservers(InsertLayerAction(insertLayerPosition, newLayer))
                    } else {
                        nn.addLayer(newLayer)
                        projectProvider?.project?.notifyObservers(AddLayerAction(newLayer))
                    }
                } catch (e: UserException) {
                    errorContainer.setError(e)
                    return@doAsync
                }

                insertLayerPosition = -1
                uiThread {
                    dismiss()
                }
            } catch (e: NNException) {
                errorContainer.setError(e, tableView!!)
            }
        }
    }

    private fun getLayerFromOptions(): LayerBase =
            this.inputCollector.get() as LayerBase

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentSandboxAddLayerBinding? {
        val binding = FragmentSandboxAddLayerBinding.inflate(inflater, container, false)

        tableView = binding.propTableView
        pageNumberTv = binding.pageNumber
        btnLeft = binding.btnLeft
        btnRight = binding.btnRight

        btnLeft.setOnClickListener { previousConstructor() }
        btnRight.setOnClickListener { nextConstructor() }

        this.layerTypeSelector = binding.spinnerLayerType
        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, layerTypes.strs().toTypedArray())
        with(this.layerTypeSelector) {
            adapter = aa
            setSelection(0, false)
        }
        this.layerTypeSelector.onItemSelectedListener = this

        binding.btnContinue.setOnClickListener { onLayerConfirmed() }

        Thread {
            Thread.sleep(250)
            activity?.runOnUiThread {
                this.onItemSelected(this.layerTypeSelector, null, 0, 0)
                this.layerTypeSelector.performClick()
            }
        }.start()

        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AddLayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(): AddLayerFragment = AddLayerFragment()

        const val KEY_INSERT_POSITION = "INSERT_POSITION"

        fun newInstance(insertPosition: Long): AddLayerFragment {
            val f = AddLayerFragment()
            val args = Bundle()
            args.putLong(KEY_INSERT_POSITION, insertPosition)
            f.arguments = args
            return f
        }
    }
}// Required empty public constructor
