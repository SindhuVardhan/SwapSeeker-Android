package com.example.swapseeker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swapseeker.Adapter.CartAdapter
import com.example.swapseeker.Adapter.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var userId: String
    private var totalPrice: Double = 0.0 // Added a variable to store the total price

    private val cartItems = mutableListOf<Product>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var cartReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        checkoutButton = findViewById(R.id.checkoutButton)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")
        cartReference = FirebaseDatabase.getInstance().reference.child("cartItems")

        fetchCartItems()

        val adapter = CartAdapter(cartItems, cartReference) {
            updateTotalPrice()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        checkoutButton.setOnClickListener {
            // Prepare product details to pass to CheckoutActivity
            val productName = cartItems.joinToString { it.productName }
            val productImageUrls = cartItems.map { it.imageUrl }.toTypedArray()
            val price = cartItems.map { it.price }.toTypedArray()

            // Create intent and pass product details
            val intent = Intent(this@CartActivity, CheckoutActivity::class.java).apply {
                putExtra("productId", productName)
                putExtra("productName", productName)
                putExtra("totalPrice", totalPrice) // Pass the total price
                putExtra("imageUrl", productImageUrls)
                putExtra("price", price)
            }
            startActivity(intent)
        }

        updateTotalPrice()
    }

    private fun fetchCartItems() {
        cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (cartSnapshot in snapshot.children) {
                    val cartItem = cartSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        if (it.myCartId == userId) { // Check if userId matches myCartId
                            val productId = it.productId
                            productId?.let { fetchProductDetails(it) }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error fetching cart items
            }
        })
    }

    private fun fetchProductDetails(productId: String) {
        val productRef = databaseReference.child(productId)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let {
                        cartItems.add(it)
                        recyclerView.adapter?.notifyDataSetChanged()
                        updateTotalPrice()
                    }
                } else {
                    // Handle case where product with productId is not found
                    // For example, show a toast message or display an error in UI
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error fetching product details
            }
        })
    }

    override fun onBackPressed() {
        // Create intent to pass data back to MainActivity
        val intent = Intent().apply {
            putExtra("fragmentToNavigate", "home") // Replace "home" with the fragment you want to navigate to
        }
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice() {
        totalPrice = calculateTotalPrice(cartItems) // Update totalPrice when calculating
        totalPriceTextView.text = "Total: $totalPrice"
    }

    private fun calculateTotalPrice(cartItems: List<Product>): Double {
        var totalPrice = 0.0
        for (item in cartItems) {
            if (item.price.isNotEmpty()) {
                val priceWithoutCommas = item.price.replace(",", "")
                totalPrice += priceWithoutCommas.toDoubleOrNull() ?: 0.0
            }
        }
        return totalPrice
    }
}
