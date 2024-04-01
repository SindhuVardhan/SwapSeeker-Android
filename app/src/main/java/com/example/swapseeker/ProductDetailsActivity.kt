package com.example.swapseeker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.swapseeker.Adapter.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var productId: String
    private lateinit var userId: String
    private lateinit var product_name: TextView
    private lateinit var price: TextView
    private lateinit var location: TextView
    private lateinit var description: TextView
    private lateinit var contactName: TextView
    private lateinit var contactEmail: TextView
    private lateinit var contactPhone: TextView
    private lateinit var contactWay: TextView
    private lateinit var buy: Button
    private lateinit var imageViewPager: ViewPager
    private lateinit var reportButton: Button
    private lateinit var whatsappButton: Button
    private lateinit var addedToCartMessage: TextView
    private lateinit var loginbutton: Button
    private lateinit var loginmessage: TextView
    private var isLoggedIn: Boolean = false
    private var contact: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        // Initialize views
        product_name = findViewById(R.id.product_name)
        price = findViewById(R.id.price)
        location = findViewById(R.id.location)
        description = findViewById(R.id.description)
        contactName = findViewById(R.id.contactName)
        contactEmail = findViewById(R.id.contactEmail)
        contactPhone = findViewById(R.id.contactPhone)
        contactWay = findViewById(R.id.contactWay)
        buy = findViewById(R.id.buy)
        imageViewPager = findViewById(R.id.imageViewPager)
        reportButton = findViewById(R.id.reportButton)
        addedToCartMessage = findViewById(R.id.addedToCartMessage)
        whatsappButton = findViewById(R.id.whatsappButton)
        loginbutton = findViewById(R.id.loginButton)
        loginmessage = findViewById(R.id.loginMessage)

        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        val backIcon = findViewById<ImageView>(R.id.backicon)

        // Retrieve product ID and user ID from intent
        productId = intent.getStringExtra("productId") ?: ""
        val currentUser = FirebaseAuth.getInstance().currentUser
        userId = currentUser?.uid ?: ""

        // Fetch product details from Firebase Realtime Database
        fetchProductDetails(productId)
        backIcon.setOnClickListener {
            onBackPressed() // This will simulate the back button press, effectively navigating back
        }


        val cartIcon = findViewById<ImageView>(R.id.carticon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("CART", "cart")
            startActivity(intent)
        }

        whatsappButton.setOnClickListener {
            val phoneNumber = contact // Replace this with the phone number you want to chat with
            if (phoneNumber != null) {
                openWhatsAppChat(phoneNumber)
            }
        }

        reportButton.setOnClickListener {
            // If user is logged in, proceed to report seller
            if (userId.isNotBlank()) {
                reportSeller(userId, productId)
            } else {
                // Handle case where user is not logged in
                // You can show a message or prompt the user to log in
            }
        }

        loginbutton.setOnClickListener {
            // Start login activity
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun openWhatsAppChat(phoneNumber: String) {
        // Create a URI with the specified phone number
        val uri = Uri.parse("https://wa.me/$phoneNumber")

        // Create an intent with ACTION_VIEW and the URI
        val intent = Intent(Intent.ACTION_VIEW, uri)

        // Set the package to WhatsApp
        intent.setPackage("com.whatsapp")

        // Verify that WhatsApp is installed on the device
        if (intent.resolveActivity(packageManager) != null) {
            // Start the activity
            startActivity(intent)
        } else {
            Toast.makeText(this, "Install WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchProductDetails(productId: String) {
        val productRef = databaseReference.child(productId)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let { updateUI(it) }
                } else {
                    Log.e("ProductDetailsActivity", "Product with productId $productId not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProductDetailsActivity", "Error fetching product details: $error")
            }
        })
    }

    fun onDataChange(snapshot: DataSnapshot) {
        if (snapshot.exists() && !isDestroyed && !isFinishing) {
            val product = snapshot.getValue(Product::class.java)
            product?.let { updateUI(it) }
        } else {
            Log.e("ProductDetailsActivity", "Product with productId $productId not found")
        }
    }

    private fun updateUI(product: Product) {
        // Update UI with product details
        product_name.text = " ${product.productName}"
        price.text = "Price: ₹${product.price}"
        location.text = "Location: ${product.location}"
        description.text = " ${product.description}"

        // Load images using Glide library
        val imageUrls = listOf(
            product.imageUrl,
            product.imageUrl2,
            product.imageUrl3,
            product.imageUrl4
        )
        val imagePagerAdapter = ImagePagerAdapter(this, imageUrls)
        imageViewPager.adapter = imagePagerAdapter

        // Pass product details to CartActivity when buy button is clicked
        buy.setOnClickListener {
            val cartItem = CartItem(
                productId = productId,
                myCartId = userId // Push user ID to myCartId field
            )
            saveCartItemToFirebase(cartItem)
            addedToCartMessage.visibility = View.VISIBLE
        }

        // Check if the user is logged in
        val isLoggedIn = userId.isNotBlank()

        // Hide contact details if the user is not logged in
        if (!isLoggedIn) {
            contactName.visibility = View.INVISIBLE
            contactEmail.visibility = View.INVISIBLE
            contactPhone.visibility = View.INVISIBLE
            contactWay.visibility = View.INVISIBLE
            // Hide WhatsApp button and its associated icon if user is not logged in
            whatsappButton.visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.waicon).visibility = View.INVISIBLE
            loginmessage.visibility = View.VISIBLE
            loginbutton.visibility = View.VISIBLE
        } else {
            // Set contact details if the user is logged in
            contactName.text = "Contact Name: ${product.contactName}"
            contactEmail.text = "Contact Email: ${product.contactEmail}"
            contactPhone.text = "Contact Phone: ${product.contactPhone}"
            contactWay.text = "Contact Way: ${product.contactWay}"
            contact = product.contactPhone
            loginmessage.visibility = View.GONE
            loginbutton.visibility = View.GONE


        }
    }


    private fun saveCartItemToFirebase(cartItem: CartItem) {
        val cartItemRef = FirebaseDatabase.getInstance().reference.child("cartItems")
        val query = cartItemRef.orderByChild("productId").equalTo(cartItem.productId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Product already exists in the cart, do not save again
                    Log.d("ProductDetailsActivity", "Product already exists in the cart")
                } else {
                    // Product does not exist in the cart, save it
                    val newCartItemRef = cartItemRef.push()
                    newCartItemRef.setValue(cartItem)
                        .addOnSuccessListener {
                            // Cart item saved successfully
                            Log.d("ProductDetailsActivity", "Cart item saved to Firebase: $newCartItemRef.key")
                            // Optionally, you can display a message or perform any other action upon successful saving
                        }
                        .addOnFailureListener { error ->
                            // Error saving cart item
                            Log.e("ProductDetailsActivity", "Error saving cart item to Firebase: $error")
                            // Optionally, you can display an error message or perform any other action upon failure
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ProductDetailsActivity", "Error checking cart for existing product: $databaseError")
            }
        })
    }

    private fun reportSeller(userId: String, productId: String) {
        // Check if the user is logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is logged in, proceed with reporting

            // Get reference to "reports" node in Firebase
            val reportsRef = FirebaseDatabase.getInstance().reference.child("reports")

            // Set reportId equal to userId
            val reportId = userId

            // Create a Report object
            val report = Report(reportId, productId)

            // Push report to Firebase database
            reportsRef.child(reportId).setValue(report)
                .addOnSuccessListener {
                    // Report saved successfully
                    Log.d("ProductDetailsActivity", "Seller reported successfully")
                    // Optionally, you can display a message or perform any other action upon successful saving
                }
                .addOnFailureListener { error ->
                    // Error saving report
                    Log.e("ProductDetailsActivity", "Error reporting seller: $error")
                    // Optionally, you can display an error message or perform any other action upon failure
                }
        } else {
            // User is not logged in, show a message asking the user to log in first
            Toast.makeText(this, "Please login first to report the seller", Toast.LENGTH_SHORT).show()
        }
    }

}
