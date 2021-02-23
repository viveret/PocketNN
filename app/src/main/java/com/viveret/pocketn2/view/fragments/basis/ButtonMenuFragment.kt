package com.viveret.pocketn2.view.fragments.basis

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import com.viveret.pocketn2.async.MenuItemSelectedListener

abstract class ButtonMenuFragment: androidx.fragment.app.DialogFragment() {
    abstract val titleString: Int
    private var mListener: MenuItemSelectedListener? = null
    protected abstract val buttonIds: IntArray
    protected abstract val submenuIds: Map<Int, Int>
    protected abstract val rootLayoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(rootLayoutId, container, false)

        val btnSwitchPage = View.OnClickListener { vBtn -> mListener!!.onActionSelected(vBtn.id) }
        for (buttonId in buttonIds) {
            v.findViewById<Button>(buttonId).setOnClickListener(btnSwitchPage)
        }

        for (submenuId in submenuIds) {
            val submenuBtn = v.findViewById<Button>(submenuId.key)
            submenuBtn.setOnClickListener { btn ->
                val popup = PopupMenu(context, btn)
                val inflater2 = popup.menuInflater
                inflater2.inflate(submenuId.value, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item -> mListener!!.onActionSelected(item.itemId) }
            }
        }

        return v
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireActivity())
                .setView(onCreateView(requireActivity().layoutInflater, null, savedInstanceState))
                .setTitle(getString(titleString))
                .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MenuItemSelectedListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement MenuButtonListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
}