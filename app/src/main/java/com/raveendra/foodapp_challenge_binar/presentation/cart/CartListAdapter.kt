package com.raveendra.foodapp_challenge_binar.presentation.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.raveendra.foodapp_challenge_binar.model.Cart
import com.raveendra.foodapp_challenge_binar.model.CartProduct
import com.raveendra.foodapp_challenge_binar.R
import com.raveendra.foodapp_challenge_binar.core.ViewHolderBinder
import com.raveendra.foodapp_challenge_binar.databinding.ItemCartProductBinding
import com.raveendra.foodapp_challenge_binar.databinding.ItemCartProductOrderBinding
import com.raveendra.foodapp_challenge_binar.util.doneEditing
import com.raveendra.foodapp_challenge_binar.util.toIdrCurrency

class CartListAdapter(private val cartListener: CartListener? = null) :
    RecyclerView.Adapter<ViewHolder>() {

    private val dataDiffer =
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<CartProduct>() {
            override fun areItemsTheSame(
                oldItem: CartProduct,
                newItem: CartProduct
            ): Boolean {
                return oldItem.cart.id == newItem.cart.id && oldItem.food.id == newItem.food.id
            }

            override fun areContentsTheSame(
                oldItem: CartProduct,
                newItem: CartProduct
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        })

    fun submitData(data: List<CartProduct>) {
        dataDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (cartListener != null) CartViewHolder(
            ItemCartProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), cartListener
        ) else CartOrderViewHolder(
            ItemCartProductOrderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = dataDiffer.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as ViewHolderBinder<CartProduct>).bind(dataDiffer.currentList[position])
    }

}

class CartViewHolder(
    private val binding: ItemCartProductBinding,
    private val cartListener: CartListener?
) : ViewHolder(binding.root), ViewHolderBinder<CartProduct> {
    override fun bind(item: CartProduct) {
        setCartData(item)
        setCartNotes(item)
        setClickListeners(item)
    }

    private fun setCartData(item: CartProduct) {
        with(binding) {
            binding.ivProductImage.load(item.food.productImgUrl) {
                crossfade(true)
            }
            tvTotalQty.text = item.cart.itemQuantity.toString()
            tvProductName.text = item.food.title
            tvProductPrice.text = (item.cart.itemQuantity * item.food.price).toIdrCurrency()
        }
    }

    private fun setCartNotes(item: CartProduct) {
        binding.etNotesItem.setText(item.cart.itemNotes)
        binding.etNotesItem.doneEditing {
            binding.etNotesItem.clearFocus()
            val newItem = item.cart.copy().apply {
                itemNotes = binding.etNotesItem.text.toString().trim()
            }
            cartListener?.onUserDoneEditingNotes(newItem)
        }
    }

    private fun setClickListeners(item: CartProduct) {
        with(binding) {
            clMinusQty.setOnClickListener { cartListener?.onMinusTotalItemCartClicked(item.cart) }
            clPlusQty.setOnClickListener { cartListener?.onPlusTotalItemCartClicked(item.cart) }
        }
    }
}

class CartOrderViewHolder(
    private val binding: ItemCartProductOrderBinding,
) : ViewHolder(binding.root), ViewHolderBinder<CartProduct> {
    override fun bind(item: CartProduct) {
        setCartData(item)
        setCartNotes(item)
    }

    @SuppressLint("SetTextI18n")
    private fun setCartData(item: CartProduct) {
        with(binding) {
            binding.ivProductImage.load(item.food.productImgUrl) {
                crossfade(true)
            }
            tvProductName.text = "${item.food.title} x${item.cart.itemQuantity}"
            tvProductPrice.text = (item.cart.itemQuantity * item.food.price).toIdrCurrency()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCartNotes(item: CartProduct) {
        binding.tvFoodNotes.text =  binding.root.context.getString(R.string.label_note_with_string, item.cart.itemNotes)
    }

}


interface CartListener {
    fun onPlusTotalItemCartClicked(cart: Cart)
    fun onMinusTotalItemCartClicked(cart: Cart)
    fun onUserDoneEditingNotes(cart: Cart)
}