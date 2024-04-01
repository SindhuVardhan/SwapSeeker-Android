package com.example.swapseeker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(private val addresses: List<String>) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentItem = addresses[position]
        holder.addressTextView.text = currentItem
    }

    override fun getItemCount() = addresses.size
}
