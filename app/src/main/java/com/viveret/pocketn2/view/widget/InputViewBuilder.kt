package com.viveret.pocketn2.view.widget

import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.holders.InputViewHolder
import com.viveret.pocketn2.view.input.ConstResolver
import com.viveret.pocketn2.view.input.GenericViewInputResolver
import com.viveret.pocketn2.view.input.ViewInputResolver
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.error.UserException
import com.viveret.tinydnn.layer.LayerBase
import com.viveret.tinydnn.layer.Padding
import com.viveret.tinydnn.reflection.annotations.CustomUserField
import com.viveret.tinydnn.reflection.annotations.UserField
import com.viveret.tinydnn.reflection.annotations.UserFields
import java.lang.reflect.Constructor
import kotlin.math.floor
import kotlin.math.sqrt

class InputViewBuilder(val layoutInflater: LayoutInflater, val root: ViewGroup, val constructor: Constructor<*>, val prevLayer: LayerBase?, val nextLayer: LayerBase?, val challenge: ChallengeMetaInfo?) {
    private val container = layoutInflater.inflate(R.layout.widget_input_container, root, false) as ViewGroup
    private val inputViewsAndResolvers = ArrayList<Pair<View, ViewInputResolver>>()

    init {
        for (param in constructor.parameterAnnotations) {
            for (annot in param) {
                when (annot) {
                    is UserField -> add2(annot)
                    is CustomUserField -> add(annot.name, annot.hint, annot.inputType)
                }
            }
        }
    }

//    fun add(type: Class<*>): InputViewBuilder {
//        for (c in type.constructors) {
//            if (c.annotations.filterIsInstance(UserConstructor::class.java).isNotEmpty()) {
//                this.add(c)
//            }
//        }
//        return this
//    }

    private fun add2(field: UserField): InputViewBuilder {
        when (field.type) {
            UserFields.InChannels -> this.add(R.string.in_channels, R.string.in_channels_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.InDim -> {
                if (challenge != null && prevLayer == null) {
                    this.addConstant(ConstResolver(challenge.inputSize), "In Dim: ${challenge.inputSize}")
                } else if (prevLayer != null) {
                    this.addConstant(ConstResolver(prevLayer.out_data_size()), "In Dim: ${prevLayer.out_data_size()}")
                } else {
                    add(R.string.in_dim, R.string.in_dim_hint, InputType.TYPE_CLASS_NUMBER)
                }
            }
            UserFields.OutDim -> add(R.string.out_dim, R.string.out_dim_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.InWidth -> {
                if (challenge != null) {
                    val v = floor(sqrt(challenge.inputSize.toDouble())).toLong()
                    this.addConstant(ConstResolver(v), "In Width: $v")
                } else {
                    add(R.string.in_width, R.string.in_width_hint, InputType.TYPE_CLASS_NUMBER)
                }
            }
            UserFields.HasBias -> addCheckbox(R.string.has_bias, R.string.has_bias_hint, true)
            UserFields.InHeight -> {
                if (challenge != null) {
                    val v = floor(sqrt(challenge.inputSize.toDouble())).toLong()
                    this.addConstant(ConstResolver(v), "In Height: $v")
                } else {
                    add(R.string.in_height, R.string.in_height_hint, InputType.TYPE_CLASS_NUMBER)
                }
            }
            UserFields.PoolSize -> add(R.string.pool_size, R.string.pool_size_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.PoolSizeX -> add(R.string.pool_size_x, 0, InputType.TYPE_CLASS_NUMBER)
            UserFields.PoolSizeY -> add(R.string.pool_size_y, 0, InputType.TYPE_CLASS_NUMBER)
            UserFields.StrideX -> add(R.string.stride_x, R.string.stride_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.StrideY -> add(R.string.stride_y, R.string.stride_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.Padding -> addToggleValue(R.string.pad_type, 0, true)
            UserFields.Stride -> add(R.string.stride, R.string.stride_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.OutChannels -> add(R.string.out_channels, R.string.out_channels_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.WindowSize -> add(R.string.window_size, R.string.window_size_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.WindowWidth -> add(R.string.window_w, R.string.window_size_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.WindowHeight -> add(R.string.window_h, R.string.window_size_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.Bias -> add(R.string.bias, R.string.bias_hint, InputType.TYPE_CLASS_NUMBER)
            UserFields.PreviousLayer -> addPreviousLayer()
        }
        return this
    }

    private fun addPreviousLayer() =
            this.addConstant(ConstResolver(prevLayer!!), "Previous Layer: $prevLayer")

    private fun addConstant(r: ViewInputResolver, text: String) {
        val tv = TextView(this.layoutInflater.context)
        tv.text = text
        val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tv.layoutParams = p
        this.container.addView(tv)
        this.inputViewsAndResolvers.add(Pair<View, ViewInputResolver>(tv, r))
    }

    private fun addCheckbox(label: Int, hint: Int, defaultValue: Boolean) {
        val cb = this.createCheckboxView(defaultValue)
        val row = LinearLayout(layoutInflater.context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        row.addView(createLabelView(label))
        row.addView(cb)
        this.inputViewsAndResolvers.add(Pair<View, ViewInputResolver>(cb, resolveCheckbox))
        this.container.addView(row)
    }

    private fun addToggleValue(label: Int, hint: Int, defaultValue: Boolean) {
        val cb = this.createCheckboxView(defaultValue)
        val row = LinearLayout(layoutInflater.context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        row.addView(createLabelView(label))
        row.addView(cb)
        this.inputViewsAndResolvers.add(Pair<View, ViewInputResolver>(cb, resolvePadding))
        this.container.addView(row)
    }

    private fun createCheckboxView(defaultValue: Boolean): CheckBox {
        val cb = CheckBox(this.layoutInflater.context)
        cb.isChecked = defaultValue
        val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        cb.layoutParams = p
        return cb
    }

    private fun add(label: String, hint: String, inputType: Int) {
        val row = LinearLayout(layoutInflater.context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        row.addView(createLabelView(label))
        row.addView(this.createInputView(hint, label, inputType))
        this.container.addView(row)
    }

    private fun add(label: Int, hint: Int, inputType: Int) {
        val row = LinearLayout(layoutInflater.context)
        row.orientation = LinearLayout.HORIZONTAL
        row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        row.addView(createLabelView(label))
        row.addView(this.createInputView(hint, label, inputType))
        this.container.addView(row)
    }

    private fun createLabelView(label: String): View {
        val v = TextView(layoutInflater.context)
        v.text = if (label.first() == '@') v.context.getString(v.context.resources.getIdentifier(label.substring(1), "string", "com.viveret.pocketn2")) else label
        v.gravity = Gravity.CENTER_VERTICAL

        val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        v.layoutParams = p
        return v
    }

    private fun createLabelView(label: Int): View {
        val v = TextView(layoutInflater.context)
        if (label > 0) {
            v.setText(label)
        }
        v.gravity = Gravity.CENTER_VERTICAL

        val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        v.layoutParams = p
        return v
    }

    private fun createInputView(hint: String, id: String, inputType: Int): View {
        var v: View? = null//layerConstructor.overrideView(propertyId, this.layoutInflater.context!!)
        val r: ViewInputResolver
        if (v == null) {
            v = EditText(this.layoutInflater.context)
            //layerConstructor.applyFieldProps(propertyId, v)
        }

        if (v is EditText) {
            v.hint = if (hint.first() == '@') v.context.getString(v.context.resources.getIdentifier(hint.substring(1), "string", "com.viveret.pocketn2")) else hint

            if (inputType != 0) {
                v.inputType = inputType
                if (inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL) {
                    v.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                }
            }

            r = when (inputType) {
                InputType.TYPE_CLASS_NUMBER -> resolveLong
                InputType.TYPE_NUMBER_FLAG_DECIMAL -> resolveDouble
                0 -> resolveString
                else -> throw Exception("Unsupported input type $inputType")
            }
        } else {
            throw Exception("Unsupported input view $v")
        }

        val p = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        p.weight = 1.0f
        v.layoutParams = p

        this.inputViewsAndResolvers.add(Pair<View, ViewInputResolver>(v, r))
        return v
    }

    private fun createInputView(hint: Int, id: Int, inputType: Int): View {
        var v: View? = null//layerConstructor.overrideView(propertyId, this.layoutInflater.context!!)
        val r: ViewInputResolver
        if (v == null) {
            v = EditText(this.layoutInflater.context)
            //layerConstructor.applyFieldProps(propertyId, v)
        }

        if (v is EditText) {
            if (hint > 0) {
                v.setHint(hint)
            }

            if (inputType != 0) {
                v.inputType = inputType
                if (inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL) {
                    v.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                }
            }

            r = when (inputType) {
                InputType.TYPE_CLASS_NUMBER -> resolveLong
                InputType.TYPE_NUMBER_FLAG_DECIMAL -> resolveDouble
                0 -> resolveString
                else -> throw Exception("Unsupported input type $inputType")
            }
        } else {
            throw Exception("Unsupported input view $v")
        }

        val p = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        p.weight = 1.0f
        v.layoutParams = p

        this.inputViewsAndResolvers.add(Pair<View, ViewInputResolver>(v, r))
        return v
    }

    fun build(): InputViewHolder {
        root.addView(this.container)
        return InputViewHolder(this.constructor, this.inputViewsAndResolvers.toTypedArray())
    }

    companion object {
        val resolveLong = GenericViewInputResolver({ v ->
            val text = v.text.toString()
            if (text.isNotBlank()) {
                text.toLongOrNull() ?: throw UserException("Input invalid", relatedView = v)
            } else {
                throw UserException("Input required", relatedView = v)
            }
        }, { v -> v as EditText })

        val resolveDouble = GenericViewInputResolver({ v ->
            val text = v.text.toString()
            if (text.isNotBlank()) {
                text.toDoubleOrNull() ?: throw UserException("Input invalid", relatedView = v)
            } else {
                throw UserException("Input required", relatedView = v)
            }
        }, { v -> v as EditText })

        val resolveString = GenericViewInputResolver({ v ->
            val text = v.text.toString()
            if (text.isNotBlank()) {
                text
            } else {
                throw UserException("Input required", relatedView = v)
            }
        }, { v -> v as EditText })

        val resolveCheckbox = GenericViewInputResolver({ v -> v.isChecked }, { v -> v as CheckBox })

        val resolvePadding = GenericViewInputResolver({ v -> if (v.isChecked) Padding.Fill else Padding.None }, { v -> v as CheckBox })
    }
}