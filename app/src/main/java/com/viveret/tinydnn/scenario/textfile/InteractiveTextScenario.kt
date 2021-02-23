package com.viveret.tinydnn.data.scenario.textfile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.viewbinding.ViewBinding
import com.viveret.pocketn2.view.fragments.basis.ScenarioFragment
import com.viveret.tinydnn.basis.DataRole
import com.viveret.tinydnn.basis.Vect
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.util.async.OnSelectedResult
import java.util.*

abstract class InteractiveTextScenario<T : ViewBinding> : ScenarioFragment<T>(), TextWatcher {
    private var changeDueToHistory = false
    protected var undoHistory = LinkedList<String>()
    protected var redoHistory = LinkedList<String>()

    protected lateinit var input: EditText
    protected lateinit var btnUndo: ImageButton
    protected lateinit var btnRedo: ImageButton

    abstract fun getFitToValues(): FloatArray

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!changeDueToHistory) {
            undoHistory.push(s!!.toString())
            this.redoHistory.clear()
            btnUndo.isEnabled = true
            btnRedo.isEnabled = false
        }
    }

    override fun submit(): OnSelectedResult {
        val s = this.input.text.toString().toLowerCase()
        val inputVect = arrayOf(Vect(FloatArray(projectProvider!!.project!!.get().in_data_size().toInt()) { i -> if (i < s.length) s[i].toFloat() / 255.0f else 0.0f }, projectProvider!!.project!!.get().in_data_size().toInt()))
        val dv = DataValues()

        dv[DataRole.Input] = DataValues.Role(inputVect, emptyArray())
        if (fitToOutput) {
            dv[DataRole.FitTo] = DataValues.Role(arrayOf(Vect(this.getFitToValues(), projectProvider!!.project!!.get().out_data_size().toInt())), emptyArray())
        }

        //this.submitData(dv)
        return OnSelectedResult(true)
    }

    fun undo() {
        changeDueToHistory = true
        this.redoHistory.push(this.input.text.toString())
        this.input.setText(undoHistory.pop())
        changeDueToHistory = false

        if (this.undoHistory.size == 0) {
            btnUndo.isEnabled = false
        }
        btnRedo.isEnabled = true
    }

    fun redo() {
        changeDueToHistory = true
        this.undoHistory.push(this.input.text.toString())
        this.input.setText(redoHistory.pop())
        changeDueToHistory = false

        if (this.redoHistory.size == 0) {
            btnRedo.isEnabled = false
        }
        btnUndo.isEnabled = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)!!

        btnUndo.setOnClickListener { undo() }
        btnRedo.setOnClickListener { redo() }
        input.addTextChangedListener(this)

        btnRedo.isEnabled = false
        btnUndo.isEnabled = false

        return v
    }
}