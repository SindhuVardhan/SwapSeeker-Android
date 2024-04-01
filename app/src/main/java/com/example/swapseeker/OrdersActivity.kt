package com.example.swapseeker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var noProductsTextView: TextView
    private lateinit var userId: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        noProductsTextView = findViewById(R.id.noProductsTextView)

        databaseReference = FirebaseDatabase.getInstance().reference.child("orders")

        fetchOrderedProducts()
    }

    private fun fetchOrderedProducts() {
        databaseReference.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<Product>()
                    for (orderSnapshot in snapshot.children) {
                        val productName = orderSnapshot.child("productName").getValue(String::class.java)
                        val productPrice = orderSnapshot.child("price").getValue(String::class.java)  // Assuming price is stored as a String
                        val imageUrl = orderSnapshot.child("imageUrl").getValue(String::class.java)

                        val product = Product(productName ?: "", productPrice ?: "", imageUrl ?: "")
                        products.add(product)
                    }
                    if (products.isNotEmpty()) {
                        showProducts(products)
                    } else {
                        noProductsTextView.visibility = TextView.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    noProductsTextView.visibility = TextView.VISIBLE
                }
            })
    }

    private fun showProducts(products: List<Product>) {
        ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrdersActivity)
            adapter = OrdersAdapter(products)
        }
    }

    private inner class OrdersAdapter(private val products: List<Product>) :
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
}
