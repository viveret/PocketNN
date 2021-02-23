package com.viveret.pocketn2.view.fragments.knowledge

import com.viveret.pocketn2.R
import com.viveret.pocketn2.view.fragments.basis.ButtonMenuFragment

class KnowledgeSourceFragment: ButtonMenuFragment() {
    override val titleString: Int = R.string.title_dialog_knowlege_source
    override val rootLayoutId: Int = R.layout.fragment_knowledge_source
    override val buttonIds: IntArray = intArrayOf(R.id.action_knowledge_catalog, R.id.action_knowledge_file)
    override val submenuIds: Map<Int, Int> = emptyMap()
}