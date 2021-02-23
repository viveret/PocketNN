package com.viveret.pocketn2.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.ActivityChallengeBinding
import com.viveret.pocketn2.view.fragments.challenge.ChallengeFragment
import com.viveret.pocketn2.view.fragments.challenge.ChallengeListFragment
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.network.SequentialNetworkModelWithWeights
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.actions.GiveUpAction
import com.viveret.tinydnn.util.async.OnSelectedResult
import org.jetbrains.anko.doAsync
import java.util.*

class ChallengeActivity: ProjectActivity() {
    private lateinit var binding: ActivityChallengeBinding
    lateinit var challenge: ChallengeMetaInfo
    override val project: NeuralNetProject = NeuralNetProject(SequentialNetworkModelWithWeights())

    override fun onActionSelected(id: Int): Boolean {
        return when (id) {
            R.id.nav_give_up -> this.giveUp()
            else -> super.onActionSelected(id)
        }
    }

    override fun getDrawerLayout(): DrawerLayout = binding.drawerLayout

    override fun getToolbar(): Toolbar = binding.toolbar

    private fun giveUp(): Boolean {
        doAsync {
            val n = project.get() as SequentialNetworkModelWithWeights
            n.removeLayers()
            challenge.giveUp(n)
            runOnUiThread { project.notifyObservers(GiveUpAction()) }
        }

        return true
    }

    override fun onSelected(item: Any): OnSelectedResult {
        when (item) {
            else -> super.onSelected(item)
        }
        return OnSelectedResult(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        project.addObserver(this)
        DataManager.get(this).putProject(project)

        val args = intent.extras
        if (args != null) {
            if (args.containsKey(ARG_CHALLENGE_ID)) {
                val id = UUID.fromString(args.getString(ARG_CHALLENGE_ID))
                this.challenge = DataManager.get(this).challenges[id]!!
                switchToFragment(ChallengeFragment.newInstance())
                lastFragment = null
                return
            }
        }
        switchToFragment(ChallengeListFragment.newInstance(1))
    }

    override fun CreateBinder(): View {
        binding = ActivityChallengeBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun predictUsingDataMethod(dataMethod: DataMethod) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun predictUsingScenario(scenario: Scenario) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val ARG_CHALLENGE_ID = "challenge-id"
    }
}