package com.webkam.app.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.webkam.app.data.model.Product
import com.webkam.app.databinding.ItemProductBinding
import com.webkam.app.utils.CurrencyUtils
import java.io.File

class ProductAdapter(
    private val lang: String,
    private val onLongClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Product, newItem: Product) =
                oldItem == newItem
        }
    }

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        with(holder.binding) {
            tvProductName.text = product.name
            tvProductPrice.text = CurrencyUtils.formatPrice(product.price, lang)

            // Size label
            val sizeLabel = try {
                val size = com.webkam.app.data.model.ProductSize.valueOf(product.size)
                if (lang == "ar") size.labelAr else size.labelEn
            } catch (e: Exception) { "" }
            tvProductSize.text = sizeLabel

            // Wholesale badge
            if (product.wholesalePrice != null) {
                tvWholesale.visibility = android.view.View.VISIBLE
                tvWholesale.text = if (lang == "ar")
                    "جملة: ${CurrencyUtils.formatPrice(product.wholesalePrice, lang)}"
                else
                    "Wholesale: ${CurrencyUtils.formatPrice(product.wholesalePrice, lang)}"
            } else {
                tvWholesale.visibility = android.view.View.GONE
            }

            // Load product image
            val imageFile = File(product.imagePath)
            if (imageFile.exists()) {
                Glide.with(root.context)
                    .load(imageFile)
                    .centerCrop()
                    .placeholder(com.webkam.app.R.drawable.placeholder_product)
                    .into(ivProduct)
            } else {
                ivProduct.setImageResource(com.webkam.app.R.drawable.placeholder_product)
            }

            // Long click = delete
            root.setOnLongClickListener {
                onLongClick(product)
                true
            }
        }
    }
}
