package com.viveret.pocketn2.view.fragments.scenario

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.viveret.pocketn2.ConfigurableIsCancelable
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.FragmentScenarioCameraBinding
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.pocketn2.view.widget.VectView
import com.viveret.tinydnn.basis.ConstDataValueStream
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.data.graphics.ColorMode
import com.viveret.tinydnn.data.graphics.ColorModes
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.util.async.OnSelectedResult
import io.fotoapparat.Fotoapparat
import io.fotoapparat.error.CameraErrorCallback
import io.fotoapparat.exception.camera.CameraException
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import org.jetbrains.anko.runOnUiThread
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList


class CameraScenarioFragment : ScenarioFragment<FragmentScenarioCameraBinding>(), CameraErrorCallback, AdapterView.OnItemSelectedListener, ConfigurableIsCancelable {
    override val dismissAfterSubmit: Boolean = false
    override val initialExpansionState: Int? = null
    private lateinit var dataFormat: DataSliceReader
    private lateinit var dataView: VectView
    private lateinit var fotoapparat: Fotoapparat
    private var currentBitmap: Bitmap? = null
    private lateinit var adapterColorMode: ArrayAdapter<CharSequence>

    override fun onNothingSelected(parent: AdapterView<*>?) {
        parent?.setSelection(0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        setColorMode(ColorModes.all.single { x -> getString(x.id) == parent!!.getItemAtPosition(position).toString() })
    }

    fun setColorMode(mode: ColorMode) {
        if (mode.id != this.dataView.colorMode.id) {
            this.dataView.colorMode = mode
            val bmp = this.currentBitmap
            if (bmp != null) {
                this.dataView.updateDrawViewFromBitmap(bmp)
            }
        }
    }

    override fun invoke(ex: CameraException) {
        Log.e("com.viveret.pocketn2", ex.message, ex)
        Toast.makeText(this.context, ex.localizedMessage, Toast.LENGTH_LONG).show()
    }

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private fun startRequiresPermission() {
        val perms = arrayOf(Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this.requireContext(), *perms)) {
            // Already have permission, do the thing
            this.requireContext().runOnUiThread {
                fotoapparat.start()
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "getString(com.viveret.pocketn2.R.string.camera_and_location_rationale)",
                    RC_CAMERA_AND_LOCATION, *perms)
        }
    }

    fun rotateClockwise() {
        this.dataView.bmpMatrix.postRotate(90.0f)
        if (this.currentBitmap != null) {
            this.dataView.updateDrawViewFromBitmap(this.currentBitmap!!)
        }
    }

    fun rotateCounterClockwise() {
        this.dataView.bmpMatrix.postRotate(-90.0f)
        if (this.currentBitmap != null) {
            this.dataView.updateDrawViewFromBitmap(this.currentBitmap!!)
        }
    }

    private fun updateDrawView() {
        val pic = fotoapparat.takePicture()
        val bmp = pic.toBitmap()
        bmp.whenAvailable { bitmapPhoto ->
            if (bitmapPhoto != null) {
                this.currentBitmap = bitmapPhoto.bitmap
                dataView.updateDrawViewFromBitmap(bitmapPhoto.bitmap)
            } else {
                context?.runOnUiThread {
                    Toast.makeText(requireContext(), "No bitmap", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        this.startRequiresPermission()
    }

    override fun onStop() {
        this.fotoapparat.stop()
        super.onStop()
    }

    override fun submit(): OnSelectedResult {
        val img = Vect(this.dataView.valueInfo.pixels.map { x -> x / 255.0f }.toFloatArray(), this.projectProvider!!.project!!.get().in_data_size().toInt())
        return try {
            // val fmt = // dataFormat.mime, "", DataRole.NA, DataRole.FitTo to fitToValue
            val sub = ConstDataValueStream(if (this.fitToOutput)
                arrayOf(DataRole.Input, DataRole.InputLabels, DataRole.FitTo, DataRole.FitToLabels)
            else
                arrayOf(DataRole.Input, DataRole.InputLabels)
            )

            sub.push(listOf(img), emptyList(), DataRole.Input)
            if (this.fitToOutput) {
                val fitToValue = this.extractValues(binding!!.fitToValue, this.projectProvider!!.project!!.get().out_data_size().toInt())
                sub.push(fitToValue, emptyList(), DataRole.FitTo)
            }

            this.submitData(sub)
        } catch (e: Exception) {
            errorContainer.setError(e, binding!!.fragmentTitle)
            OnSelectedResult(false)
        }
    }

    private fun extractValues(v: EditText, requiredSize: Int): ArrayList<Vect> {
        val out = ArrayList<Vect>()
        val inputsStr = v.text.toString()
        for (row in inputsStr.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val valStrings = row.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (valStrings.size != requiredSize && requiredSize > 0) {
                val msg = "Input length ${if (valStrings.size < requiredSize) "smaller" else "greater"} than amount required ($requiredSize)"
                throw UserException(msg, IllegalArgumentException(msg), v)
            }

            val vals = FloatArray(valStrings.size)
            for (i in vals.indices) {
                vals[i] = java.lang.Float.parseFloat(valStrings[i].trim { it <= ' ' })
            }
            out.add(Vect(vals, vals.size))
        }
        return out
    }

    companion object {
        const val RC_CAMERA_AND_LOCATION = 123
    }

    override fun CreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentScenarioCameraBinding? {
        val binding = FragmentScenarioCameraBinding.inflate(inflater, container, false)

        this.dataView = VectView(binding.root.context)
        this.dataView.translatable = false
        this.dataView.holdToDraw = false
        binding.contentFrame.addView(this.dataView)
        this.dataView.setInputSize(projectProvider!!.project!!.get().in_data_size())
        this.dataView.attachFragment(this)

        val btnRotateClockwise = binding.btnRotateClockwise
        btnRotateClockwise.setOnClickListener {
            this@CameraScenarioFragment.rotateClockwise()
        }

        val btnRotateCounterClockwise = binding.btnRotateCounterClockwise
        btnRotateCounterClockwise.setOnClickListener {
            this@CameraScenarioFragment.rotateCounterClockwise()
        }

        // binding.fitToOutputInputEditText = v.findViewById(R.id.fit_to_value)
        if (this.fitToOutput) {
            (binding.fitToValue.parent as ViewGroup).visibility = View.VISIBLE
        }

        // this.titleView = v.findViewById(R.id.fragment_title)
        binding.fragmentTitle.setText(R.string.data_method_camera)

        val cameraView = binding.camera
        this.fotoapparat = Fotoapparat(this.requireContext(),
                cameraView,
                scaleType = ScaleType.CenterCrop,
                lensPosition = back(),
                cameraErrorCallback = this)

        cameraView.setOnClickListener { fotoapparat.focus() }
        binding.btnTakePicture.setOnClickListener { updateDrawView() }

        val spinColorMode = binding.spinnerColorMode
        // Create an ArrayAdapter using the string array and a default spinner layout
        this.adapterColorMode = ArrayAdapter.createFromResource(requireContext(),
                R.array.camera_color_modes, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapterColorMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinColorMode.adapter = adapterColorMode
        spinColorMode.onItemSelectedListener = this

        binding.cbColorInvert.setOnCheckedChangeListener { _, isChecked ->
            this.dataView.invertColor = isChecked;
            if (this.currentBitmap != null) {
                dataView.updateDrawViewFromBitmap(currentBitmap!!)
            }
        }

        return binding
    }

    override fun setIsCancelable(value: Boolean) {
        isCancelable = value
    }
}