package com.raveendra.foodapp_challenge_binar.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.raveendra.foodapp_challenge_binar.databinding.ItemMenuGridBinding
import com.raveendra.foodapp_challenge_binar.databinding.ItemMenuListBinding
import com.raveendra.foodapp_challenge_binar.model.Food
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency

class HomeListAdapter(
    var viewType: Int,
    private val onItemClick: (Food) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VH_MENU_LIST = 1
        const val VH_MENU_GRID = 2
    }

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    })

    fun submitData(data: List<Food>) {
        differ.submitList(data)
    }

    fun refreshList() {
        notifyItemRangeChanged(0, differ.currentList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VH_MENU_LIST -> {
                HomeListViewHolder(
                    binding = ItemMenuListBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    onItemClick = onItemClick
                )
            }

            VH_MENU_GRID -> {
                HomeGridViewHolder(
                    binding = ItemMenuGridBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    onItemClick = onItemClick
                )
            }

            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is HomeListViewHolder -> {
                holder.bind(differ.currentList[position])
            }

            is HomeGridViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}


class HomeListViewHolder(
    private val binding: ItemMenuListBinding,
    private val onItemClick: (Food) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: Food) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.ivMenuPicture.load(item.productImgUrl) {
            crossfade(true)
        }
        binding.tvMenuTitle.text = item.title
        binding.tvMenuPrice.text = item.price.toIdrCurrency()
    }
}

class HomeGridViewHolder(
    private val binding: ItemMenuGridBinding,
    private val onItemClick: (Food) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: Food) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.ivMenuPicture.load(item.productImgUrl) {
            crossfade(true)
        }
        binding.tvMenuTitle.text = item.title
        binding.tvMenuPrice.text = item.price.toIdrCurrency()
    }
}