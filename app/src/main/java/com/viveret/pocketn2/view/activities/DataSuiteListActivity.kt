package com.viveret.pocketn2.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.databinding.ActivityDataSuiteListBinding
import com.viveret.pocketn2.view.adapters.DataSuiteRecyclerViewAdapter
import com.viveret.tinydnn.basis.StreamPackage
import com.viveret.tinydnn.data.DataLifecycleListener

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [DataSuiteDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class DataSuiteListActivity : AppCompatActivity(), DataLifecycleListener {

    private lateinit var _binding: ActivityDataSuiteListBinding


    override fun onCached(item: Any) {
        if (item is StreamPackage) {
            _binding.dataBrowseList.browseDataList.adapter?.notifyDataSetChanged()
            this.updateNoContentView()
        }
    }

    override fun onFreed(item: Any) {
        if (item is StreamPackage) {
            _binding.dataBrowseList.browseDataList.adapter?.notifyDataSetChanged()
            this.updateNoContentView()
        }
    }

    init {
        DataManager.get(this).addListener(this)
    }

    override fun onResume() {
        super.onResume()
        _binding.dataBrowseList.browseDataList.adapter?.notifyDataSetChanged()
        this.updateNoContentView()
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataSuiteListBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setSupportActionBar(_binding.toolbar)
        _binding.toolbar.title = title

        _binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (_binding.dataBrowseList.detailContainer != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/loadDataValues-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        _binding.dataBrowseList.browseDataList.adapter = DataSuiteRecyclerViewAdapter(this, twoPane, DataManager.get(this))

        this.updateNoContentView()
    }

    private fun updateNoContentView() {
        if (_binding.dataBrowseList.browseDataList.adapter!!.itemCount == 0) {
            _binding.noContent.visibility = View.VISIBLE
        } else {
            _binding.noContent.visibility = View.INVISIBLE
        }
    }
}
