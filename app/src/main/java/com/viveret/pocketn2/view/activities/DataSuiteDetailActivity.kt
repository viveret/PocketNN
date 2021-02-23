package com.viveret.pocketn2.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.ActivityDataSuiteDetailBinding
import com.viveret.pocketn2.view.fragments.data.DataSuiteDetailFragment
import com.viveret.tinydnn.basis.StreamPackage
import com.viveret.tinydnn.data.DataLifecycleListener
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.ProjectProvider
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import com.viveret.tinydnn.util.async.OnSelectedResult

/**
 * An activity representing a single ObjectItemKindReplaceMe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [DataSuiteListActivity].
 */
class DataSuiteDetailActivity : AppCompatActivity(), DataLifecycleListener, OnItemSelectedListener, ProjectProvider {
    override val project: NeuralNetProject?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun onSelected(item: Any): OnSelectedResult {
        // show even more detail
        return OnSelectedResult(true)
    }

    val dm = DataManager.get(this)

    override fun onCached(item: Any) {
        if (item is StreamPackage && item.id.toString() == intent.getStringExtra(DataSuiteDetailFragment.ARG_ITEM_ID)) {
            finish()
        }
    }

    override fun onFreed(item: Any) {
        if (item is StreamPackage && item.id.toString() == intent.getStringExtra(DataSuiteDetailFragment.ARG_ITEM_ID)) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dm.addListener(this)
        val binding = ActivityDataSuiteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.detailToolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = DataSuiteDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DataSuiteDetailFragment.ARG_ITEM_ID,
                            intent.getStringExtra(DataSuiteDetailFragment.ARG_ITEM_ID))
                    putString(DataSuiteDetailFragment.ARG_PROJECT_ID,
                            intent.getStringExtra(DataSuiteDetailFragment.ARG_PROJECT_ID))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit()
        }
    }

    override fun onDestroy() {
        dm.removeListener(this)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    navigateUpTo(Intent(this, DataSuiteListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
