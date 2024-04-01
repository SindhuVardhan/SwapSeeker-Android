// CartAdapter.kt

package com.example.swapseeker.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swapseeker.Product
import com.example.swapseeker.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val cartItems: MutableList<Product>,
    private val cartReference: DatabaseReference,
    private val updateCartView: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]
        holder.productNameTextView.text = currentItem.productName
        holder.productPriceTextView.text = currentItem.price

        // Load image using Glide or Picasso
        Glide.with(holder.itemView.context)
            .load(currentItem.imageUrl) // Load the first image URL
            .placeholder(R.drawable.placeholder1) // Placeholder image while loading
            .into(holder.productImageView)

        holder.removeButton.setOnClickListener {
            removeFromCart(currentItem)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)
    }

    private fun removeFromCart(product: Product) {
        val query = cartReference.orderByChild("productId").equalTo(product.productId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    // Remove the item from the cartItems node in the database
                    snapshot.ref.removeValue().addOnSuccessListener {
                        // If removal from the database is successful, remove the item from the local list
                        cartItems.remove(product)
                        notifyDataSetChanged()
                        updateCartView()
                    }.addOnFailureListener {
                        // Handle failure to remove item from cart
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error fetching cart items
            }
        })
    }

}
