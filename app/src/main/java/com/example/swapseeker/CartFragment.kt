package com.example.swapseeker
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swapseeker.Adapter.CartAdapter
import com.example.swapseeker.Adapter.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var emptyCartTextView: TextView
    private lateinit var emptyCartAnimationView: ImageView
    private lateinit var userId: String
    private var totalPrice: Double = 0.0 // Added a variable to store the total price

    private val cartItems = mutableListOf<Product>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var cartReference: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerView = view.findViewById(R.id.cartRecyclerView)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)
        checkoutButton = view.findViewById(R.id.checkoutButton)
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView)
        emptyCartAnimationView = view.findViewById(R.id.emptyCartAnimationView)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")
        cartReference = FirebaseDatabase.getInstance().reference.child("cartItems")

        fetchCartItems()

        val adapter = CartAdapter(cartItems, cartReference) {
            updateTotalPrice()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        checkoutButton.setOnClickListener {
            // Prepare product details to pass to CheckoutActivity
            val productId = cartItems.joinToString { it.productId }
            val productName = cartItems.joinToString { it.productName }
            val productImageUrls = cartItems.map { it.imageUrl }.toTypedArray()
            val price = cartItems.map { it.price }.toTypedArray()

            // Create intent and pass product details
            val intent = Intent(requireContext(), CheckoutActivity::class.java).apply {
                putExtra("productId", productId)
                putExtra("productName", productName)
                putExtra("totalPrice", totalPrice) // Pass the total price
                putExtra("imageUrl", productImageUrls)
                putExtra("price", price)
            }
            startActivity(intent)
        }

        updateTotalPrice()

        return view
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

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice() {
        totalPrice = calculateTotalPrice(cartItems) // Update totalPrice when calculating
        totalPriceTextView.text = "Total: $totalPrice"

        // Show or hide empty cart view based on cart items
        if (cartItems.isEmpty()) {
            emptyCartTextView.visibility = View.VISIBLE
            emptyCartAnimationView.visibility = View.VISIBLE
        } else {
            emptyCartTextView.visibility = View.GONE
            emptyCartAnimationView.visibility = View.GONE
        }
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
