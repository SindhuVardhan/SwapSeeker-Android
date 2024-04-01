package com.example.swapseeker

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.swapseeker.databinding.ActivityCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {
    private lateinit var binding: ActivityCheckoutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private var totalPrice: Double = 0.0
    private lateinit var savedAddressLayout: LinearLayout
    private lateinit var productId: String
    private lateinit var productRef: DatabaseReference
    private lateinit var product: Product
    private var selectedAddressId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the binding
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productId = intent.getStringExtra("productId") ?: ""

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        productRef = FirebaseDatabase.getInstance().reference.child("products")

        // Retrieve the total price from the intent extras
        totalPrice = intent.getDoubleExtra("totalPrice", 0.0)

        // Retrieve the total price TextView
        val totalPriceTextView = binding.totalPriceTextView
        // Set the text of the total price TextView with the rupee symbol
        totalPriceTextView.text = "Total: ₹$totalPrice"

        // Retrieve the current user's ID
        val currentUser = auth.currentUser
        savedAddressLayout = binding.savedAddressLayout

        val backIcon = findViewById<ImageView>(R.id.backicon)
        backIcon.setOnClickListener {
            onBackPressed() // This will simulate the back button press, effectively navigating back
        }

        // Check if a user is signed in
        if (currentUser != null) {
            // Retrieve the user ID
            userId = currentUser.uid
            // Initialize the Firebase database reference
            databaseReference = FirebaseDatabase.getInstance().reference.child("addresses").child(userId)
            // Fetch and display saved addresses
            fetchAndDisplayAddresses()
        } else {
            // No user is signed in, handle this case accordingly (e.g., redirect to login screen)
        }

        // Fetch product details
        fetchProductDetails(productId)

        binding.addAddressButton.setOnClickListener {
            val intent = Intent(this@CheckoutActivity, AddressActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the buy button
        val buyNowButton = binding.buyNowButton
        buyNowButton.setOnClickListener {
            initiatePayment()
        }
    }

    private fun fetchProductDetails(productId: String) {
        productRef.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the product details from dataSnapshot
                product = dataSnapshot.getValue(Product::class.java) ?: Product()
                // You can access the product details here
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun fetchAndDisplayAddresses() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing address views
                savedAddressLayout.removeAllViews()

                // Iterate through each address in the dataSnapshot
                for ((index, addressSnapshot) in dataSnapshot.children.withIndex()) {
                    // Check if the address belongs to the current user
                    if (addressSnapshot.child("myAddress").getValue(String::class.java) == userId) {
                        // Convert each address to the Address object
                        val address = addressSnapshot.getValue(Address::class.java)

                        // Create a CardView for the address
                        val cardView = CardView(this@CheckoutActivity)
                        val cardViewParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        // Add margins to the CardView
                        cardViewParams.setMargins(0, if (index == 0) 0 else 32, 0, 0)

                        cardView.layoutParams = cardViewParams
                        cardView.cardElevation = 4f
                        cardView.radius = 8f

                        // Create a RadioButton for the address
                        val radioButton = RadioButton(this@CheckoutActivity)
                        radioButton.text = address?.toString()
                        radioButton.id = addressSnapshot.key.hashCode()

                        // Add the RadioButton to the CardView
                        cardView.addView(radioButton)

                        // Set a click listener for the RadioButton
                        radioButton.setOnClickListener {
                            selectedAddressId = addressSnapshot.key ?: ""
                        }

                        // Add the CardView to the LinearLayout
                        savedAddressLayout.addView(cardView)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun initiatePayment() {
        // Check if an address is selected
        if (selectedAddressId.isEmpty()) {
            Toast.makeText(this, "Please select an address", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with payment initiation
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // User is not authenticated, prompt them to log in first
            Toast.makeText(this, "Please log in to proceed with the payment", Toast.LENGTH_SHORT).show()
            return
        }

        // User is authenticated, proceed with payment initiation
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_PBuhh7QvWrBpOp")

        try {
            val options = JSONObject()
            options.put("name", "SwapSeeker")
            options.put("description", "Payment")
            options.put("currency", "INR")
            options.put("amount", (totalPrice * 100).toInt())
            val prefill = JSONObject()
            prefill.put("email", "test@razorpay.com")
            prefill.put("contact", "9999999999")
            options.put("prefill", prefill)

            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun updatePaymentStatus(successful: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Assuming you're using Firebase Authentication
        val userRef = database.reference.child("users").child(userId ?: "")

        userRef.child("paymentStatus").setValue(successful)
            .addOnSuccessListener {
                Log.d(TAG, "Payment status updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update payment status: $e")
                // Handle error updating payment status
            }
    }

    override fun onPaymentSuccess(p0: String?) {
        // Handle payment success
        // You can update your database or navigate to the success page
        Toast.makeText(this@CheckoutActivity, "Payment successful!", Toast.LENGTH_SHORT).show()
        // Example: Update database with payment status
        updatePaymentStatus(true)

        // Get the current date and time
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Upload order details to Firebase under the user's orders
        val ordersRef = FirebaseDatabase.getInstance().reference.child("orders").child(userId).push()

        // Create an order object with product details
        val orderDetails = HashMap<String, Any>()
        orderDetails["orderId"] = ordersRef.key ?: ""
        orderDetails["myOrderId"] = userId
        orderDetails["productId"] = productId
        orderDetails["productName"] = product.productName
        orderDetails["productImage"] = product.imageUrl
        orderDetails["price"] = product.price
        orderDetails["purchaseDate"] = currentDate // Add the purchase date
        // Add other product details as needed

        ordersRef.setValue(orderDetails)
            .addOnSuccessListener {
                Log.d(TAG, "Order details uploaded successfully")
                // Navigate to the success page or perform any other necessary action
                // Navigate to OrdersActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ORDER", "order")
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to upload order details: $e")
                // Handle error uploading order details
            }
    }

    
    override fun onPaymentError(p0: Int, p1: String?) {
        // Handle payment failure
        // You can display an error message to the user
        Toast.makeText(this@CheckoutActivity, "Payment failed: $p1", Toast.LENGTH_SHORT).show()
        // Example: Update UI to inform user about payment failure
        binding.paymentStatusTextView.text = "Payment failed: $p1"
        // Example: Log payment error for further analysis
        Log.e("PaymentError", "Error code: $p0, Error message: $p1")
    }
}
