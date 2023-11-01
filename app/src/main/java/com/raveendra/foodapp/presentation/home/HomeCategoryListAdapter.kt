package com.raveendra.foodapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.raveendra.foodapp.data.model.CategoryViewParam
import com.raveendra.foodapp.databinding.ItemMenuCategoryBinding

class HomeCategoryListAdapter(
    private val onItemClick: (CategoryViewParam) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<CategoryViewParam>() {
            override fun areItemsTheSame(
                oldItem: CategoryViewParam,
                newItem: CategoryViewParam
            ): Boolean {
                return oldItem.nama == newItem.nama
            }

            override fun areContentsTheSame(
                oldItem: CategoryViewParam,
                newItem: CategoryViewParam
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    fun submitData(data: List<CategoryViewParam>) {
        differ.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return HomeCategoryListViewHolder(
            binding = ItemMenuCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClick = onItemClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is HomeCategoryListViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}

class HomeCategoryListViewHolder(
    private val binding: ItemMenuCategoryBinding,
    private val onItemClick: (CategoryViewParam) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: CategoryViewParam) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.ivHeaderPromo.load(item.imageUrl)
        binding.tvHeaderName.text = item.nama
    }
}
