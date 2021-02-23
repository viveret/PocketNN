package com.viveret.pocketn2.view.fragments.main


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.viveret.pocketn2.R


/**
 * A simple [Fragment] subclass.
 * Use the [PrefsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrefsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PrefsFragment.
         */
        fun newInstance(): PrefsFragment = PrefsFragment()
    }
}// Required empty public constructor
