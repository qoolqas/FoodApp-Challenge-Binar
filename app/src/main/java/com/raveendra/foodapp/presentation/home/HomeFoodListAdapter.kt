package com.raveendra.foodapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.raveendra.foodapp.R
import com.raveendra.foodapp.data.model.FoodViewParam
import com.raveendra.foodapp.databinding.ItemMenuGridBinding
import com.raveendra.foodapp.databinding.ItemMenuListBinding
import com.raveendra.foodapp.util.toIdrCurrency

class HomeFoodListAdapter(
    var viewType: Int,
    private val onItemClick: (FoodViewParam) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val VH_MENU_LIST = 1
        const val VH_MENU_GRID = 2
    }

    private val differ = AsyncListDiffer(
        this,
        object : DiffUtil.ItemCallback<FoodViewParam>() {
            override fun areItemsTheSame(oldItem: FoodViewParam, newItem: FoodViewParam): Boolean {
                return oldItem.nama == newItem.nama
            }

            override fun areContentsTheSame(oldItem: FoodViewParam, newItem: FoodViewParam): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    )

    fun submitData(data: List<FoodViewParam>) {
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
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClick = onItemClick
                )
            }

            VH_MENU_GRID -> {
                HomeGridViewHolder(
                    binding = ItemMenuGridBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
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
    private val onItemClick: (FoodViewParam) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: FoodViewParam) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.ivMenuPicture.load(item.imageUrl) {
            crossfade(true)
        }
        binding.tvMenuTitle.text = item.nama
        binding.tvMenuPrice.text = (item.harga?.toIdrCurrency() ?: 0.0).toString()
    }
}

class HomeGridViewHolder(
    private val binding: ItemMenuGridBinding,
    private val onItemClick: (FoodViewParam) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: FoodViewParam) {
        binding.root.setOnClickListener {
            onItemClick.invoke(item)
        }
        binding.ivMenuPicture.load(item.imageUrl) {
            crossfade(true)
            error(R.drawable.img_placeholder)
        }
        binding.tvMenuTitle.text = item.nama
        binding.tvMenuPrice.text = (item.harga?.toIdrCurrency() ?: 0.0).toString()
    }
}
