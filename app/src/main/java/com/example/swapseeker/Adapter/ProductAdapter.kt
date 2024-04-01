package com.example.swapseeker.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swapseeker.Product
import com.example.swapseeker.R

class ProductAdapter(
    private val productList: List<Product>,
    private val viewType: Int,
    private var itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }
    fun setOnRentItemClickListener(listener: OnItemClickListener?) {
        itemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ALL -> ProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
            )
            VIEW_TYPE_SELL -> SellProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product_sell, parent, false)
            )
            VIEW_TYPE_RENT -> RentProductViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product_rent, parent, false)
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = productList[position]

        when (holder) {
            is ProductViewHolder -> holder.bindProduct(product)
            is SellProductViewHolder -> holder.bindSellProduct(product)
            is RentProductViewHolder -> holder.bindRentProduct(product)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.price)
        private val productImage: ImageView = itemView.findViewById(R.id.productImg)

        fun bindProduct(product: Product) {
            productName.text = "Name: ${product.productName}"
            productPrice.text = "Price: ₹${product.price}"
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(productImage)

            itemView.setOnClickListener {
                itemClickListener?.onItemClick(product)
                Log.d("ProductAdapter", "onItemClick called")
            }
        }
    }

    inner class SellProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sellProductName: TextView = itemView.findViewById(R.id.sell_name)
        private val sellPrice: TextView = itemView.findViewById(R.id.sell_price)
        private val sellImg: ImageView = itemView.findViewById(R.id.sellImg)

        fun bindSellProduct(product: Product) {
            sellProductName.text = "Name: ${product.productName}"
            sellPrice.text = "Price: ₹${product.price}"
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(sellImg)

            itemView.setOnClickListener {
                itemClickListener?.onItemClick(product)
                Log.d("ProductAdapter", "onItemClick called")
            }
        }
    }

    inner class RentProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rentProductName: TextView = itemView.findViewById(R.id.product_name_rent)
        private val rentPrice: TextView = itemView.findViewById(R.id.price_rent)
        private val rentImg: ImageView = itemView.findViewById(R.id.productImgRent)

        fun bindRentProduct(product: Product) {
            rentProductName.text = "Name: ${product.productName}"
            rentPrice.text = "Price: ₹${product.price}"
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(rentImg)

            // Set click listener on the whole item view
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(product)
                Log.d("ProductAdapter", "onItemClick called")
            }
        }
    }

    companion object {
        const val VIEW_TYPE_ALL = 0
        const val VIEW_TYPE_SELL = 1
        const val VIEW_TYPE_RENT = 2
    }
}
