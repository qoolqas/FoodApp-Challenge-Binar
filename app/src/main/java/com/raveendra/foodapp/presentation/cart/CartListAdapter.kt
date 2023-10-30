package com.raveendra.foodapp.presentation.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.raveendra.foodapp.R
import com.raveendra.foodapp.core.ViewHolderBinder
import com.raveendra.foodapp.databinding.ItemCartProductBinding
import com.raveendra.foodapp.databinding.ItemCartProductOrderBinding
import com.raveendra.foodapp.model.Cart
import com.raveendra.foodapp.util.doneEditing
import com.raveendra.foodapp.util.toIdrCurrency

class CartListAdapter(private val cartListener: CartListener? = null) :
    RecyclerView.Adapter<ViewHolder>() {

    private val dataDiffer =
        AsyncListDiffer(
            this,
            object : DiffUtil.ItemCallback<Cart>() {
                override fun areItemsTheSame(
                    oldItem: Cart,
                    newItem: Cart
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Cart,
                    newItem: Cart
                ): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }
            }
        )

    fun submitData(data: List<Cart>) {
        dataDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (cartListener != null) {
            CartViewHolder(
                ItemCartProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                cartListener
            )
        } else {
            CartOrderViewHolder(
                ItemCartProductOrderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = dataDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolderBinder<Cart>).bind(dataDiffer.currentList[position])
    }
}

class CartViewHolder(
    private val binding: ItemCartProductBinding,
    private val cartListener: CartListener?
) : ViewHolder(binding.root), ViewHolderBinder<Cart> {
    override fun bind(item: Cart) {
        setCartData(item)
        setCartNotes(item)
        setClickListeners(item)
    }

    private fun setCartData(item: Cart) {
        with(binding) {
            binding.ivProductImage.load(item.productImgUrl) {
                crossfade(true)
            }
            tvTotalQty.text = item.itemQuantity.toString()
            tvProductName.text = item.name
            tvProductPrice.text = (item.itemQuantity * item.price).toIdrCurrency()
        }
    }

    private fun setCartNotes(item: Cart) {
        binding.etNotesItem.setText(item.itemNotes)
        binding.etNotesItem.doneEditing {
            binding.etNotesItem.clearFocus()
            val newItem = item.copy().apply {
                itemNotes = binding.etNotesItem.text.toString().trim()
            }
            cartListener?.onUserDoneEditingNotes(newItem)
        }
    }

    private fun setClickListeners(item: Cart) {
        with(binding) {
            clMinusQty.setOnClickListener { cartListener?.onMinusTotalItemCartClicked(item) }
            clPlusQty.setOnClickListener { cartListener?.onPlusTotalItemCartClicked(item) }
        }
    }
}

class CartOrderViewHolder(
    private val binding: ItemCartProductOrderBinding
) : ViewHolder(binding.root), ViewHolderBinder<Cart> {
    override fun bind(item: Cart) {
        setCartData(item)
        setCartNotes(item)
    }

    @SuppressLint("SetTextI18n")
    private fun setCartData(item: Cart) {
        with(binding) {
            binding.ivProductImage.load(item.productImgUrl) {
                crossfade(true)
            }
            tvProductName.text = "${item.name} x${item.itemQuantity}"
            tvProductPrice.text = (item.itemQuantity * item.price).toIdrCurrency()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCartNotes(item: Cart) {
        binding.tvFoodNotes.text =
            binding.root.context.getString(R.string.label_note_with_string, item.itemNotes)
    }
}

interface CartListener {
    fun onPlusTotalItemCartClicked(cart: Cart)
    fun onMinusTotalItemCartClicked(cart: Cart)
    fun onUserDoneEditingNotes(cart: Cart)
}
