package com.example.treenity_constraint.data.model.mypage.tree

data class MyTreeItem (
    val treeId: Int,
    val item: Item,
    val bucket: Int,
    val level: Int,
    val createdDate: String
)