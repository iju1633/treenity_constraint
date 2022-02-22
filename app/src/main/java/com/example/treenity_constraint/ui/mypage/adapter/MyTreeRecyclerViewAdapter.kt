package com.example.treenity_constraint.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.treenity_constraint.data.model.mypage.tree.Item
import com.example.treenity_constraint.data.model.mypage.tree.MyTreeItem
import com.example.treenity_constraint.databinding.TestBinding

class MyTreeRecyclerViewAdapter(items: List<Item>) : RecyclerView.Adapter<MyTreeRecyclerViewAdapter.MyViewHolder>() {


    private val items: List<Item>
    private lateinit var mListener: OnItemClickListener

    init {
        this.items = items
    }


    inner class MyViewHolder // 지금부터 시작!
    constructor(
        val binding: TestBinding, listener: OnItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.card.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: Item) {
            binding.treeName.text = item.name
            binding.treeImage.load(item.imagePath)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {

        mListener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<MyTreeItem>() {
        override fun areItemsTheSame(oldItem: MyTreeItem, newItem: MyTreeItem): Boolean {
            return oldItem.treeId == newItem.treeId
        }

        override fun areContentsTheSame(oldItem: MyTreeItem, newItem: MyTreeItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var trees: List<MyTreeItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            TestBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = trees[position].item

        holder.apply {
            bind(item)
        }
    }

    override fun getItemCount() = trees.size
}