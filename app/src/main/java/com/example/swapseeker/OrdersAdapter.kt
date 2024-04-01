package com.example.swapseeker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrdersAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.productNameTextView.text = product.productName
        holder.productPriceTextView.text = product.price
        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.placeholder1)
            .into(holder.productImageView)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}
