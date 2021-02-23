package com.viveret.pocketn2.view.fragments.main

import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.fragments.basis.ButtonMenuFragment

class MainActivityFragment : ButtonMenuFragment() {
    override val titleString: Int = 0
    override val rootLayoutId: Int = R.layout.fragment_main
    override val buttonIds: IntArray = intArrayOf(R.id.btnSettings, R.id.btnAboutML, R.id.btnAboutApp, R.id.action_challenge_mode)
    override val submenuIds: Map<Int, Int> = mapOf(R.id.btnSandbox to R.menu.new_nn_options)
}
