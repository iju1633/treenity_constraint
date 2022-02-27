package com.example.treenity_constraint.ui.store.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.treenity_constraint.data.model.store.StoreItem
import com.example.treenity_constraint.databinding.StoreItemRowBinding



class StoreAdapter(items: List<StoreItem>) : RecyclerView.Adapter<StoreAdapter.MyViewHolder>() {


    private val items: List<StoreItem>
    private lateinit var mListener: OnItemClickListener

    init {
        this.items = items
    }

    inner class MyViewHolder
    constructor(
        val binding: StoreItemRowBinding, listener: OnItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.item.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(item: StoreItem) {
            (item.name + "   " + item.cost + "P").also { binding.treeNameAndPrice.text = it }

            // coil 이미지 로더 사용
            binding.treeImage.load(item.imagePath) {
                crossfade(true)
                crossfade(1000)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {

        mListener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<StoreItem>() {
        override fun areItemsTheSame(oldItem: StoreItem, newItem: StoreItem): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: StoreItem, newItem: StoreItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var itemList: List<StoreItem>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            StoreItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]

        holder.apply {
            bind(item)
        }
    }

    override fun getItemCount() = itemList.size
}