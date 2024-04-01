package com.example.swapseeker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProductEditActivity : AppCompatActivity() {
    private lateinit var productNameEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var productAgeEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var sellRentSpinner: Spinner
    private lateinit var contactNameEditText: EditText
    private lateinit var contactEmailEditText: EditText
    private lateinit var contactPhoneEditText: EditText
    private lateinit var contactWayEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var editSaveButton: Button
    private lateinit var visibilitySpinner: Spinner

    private lateinit var productId: String // Product ID passed from previous activity
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val categories = arrayOf(
        "SmartPhones", "Toys",
        "Property", "Old_is_Gold", "HomeAppliances", "Gaming",
        "Fashion", "Electronics", "Cosmetics",
        "Computers", "Cameras", "AutoMobiles"
    )

    private val sellRentOptions = arrayOf("Sell", "Rent")
    private val visibilityOptions = arrayOf("Visible", "Hidden")

    private var isEditMode = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_edit)

        // Initialize Firebase
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        productNameEditText = findViewById(R.id.product_name)
        categorySpinner = findViewById(R.id.category)
        productAgeEditText = findViewById(R.id.product_age)
        priceEditText = findViewById(R.id.price)
        descriptionEditText = findViewById(R.id.description)
        sellRentSpinner = findViewById(R.id.sell_rent)
        contactNameEditText = findViewById(R.id.contactName)
        contactEmailEditText = findViewById(R.id.contactEmail)
        contactPhoneEditText = findViewById(R.id.contactPhone)
        contactWayEditText = findViewById(R.id.contactWay)
        locationEditText = findViewById(R.id.location)
        editSaveButton = findViewById(R.id.editSaveButton)
        visibilitySpinner = findViewById(R.id.visibility_spinner)

        // Retrieve product ID from intent
        productId = intent.getStringExtra("productId") ?: ""

        val backIcon = findViewById<ImageView>(R.id.backicon)
        backIcon.setOnClickListener {
            onBackPressed() // This will simulate the back button press, effectively navigating back
        }

        // Populate spinners with categories, sell/rent options, and visibility options
        populateCategorySpinner()
        populateSellRentSpinner()
        populateVisibilitySpinner()

        // Load product details from Firebase
        loadProductDetails(productId)

        // Set up click listener for edit/save button
        editSaveButton.setOnClickListener {
            if (isEditMode) {
                // Save edited product details
                updateProductDetails(productId)
                // Disable editing mode
                setEditMode(false)
            } else {
                // Enable editing mode
                setEditMode(true)
            }
        }
    }

    private fun populateCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun populateSellRentSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sellRentOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sellRentSpinner.adapter = adapter
    }

    private fun populateVisibilitySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, visibilityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        visibilitySpinner.adapter = adapter
    }

    private fun loadProductDetails(productId: String) {
        val productsRef = database.reference.child("products").child(productId)

        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product = dataSnapshot.getValue(Product::class.java)
                if (product != null) {
                    // Populate UI elements with product details
                    productNameEditText.setText(product.productName)
                    val categoryIndex = getCategoryIndex(product.category)
                    categorySpinner.setSelection(categoryIndex)
                    productAgeEditText.setText(product.productAge)
                    priceEditText.setText(product.price)
                    descriptionEditText.setText(product.description)
                    val sellRentIndex = getSellRentIndex(product.sellRent)
                    sellRentSpinner.setSelection(sellRentIndex)
                    contactNameEditText.setText(product.contactName)
                    contactEmailEditText.setText(product.contactEmail)
                    contactPhoneEditText.setText(product.contactPhone)
                    contactWayEditText.setText(product.contactWay)
                    locationEditText.setText(product.location)
                    val visibilityIndex = getVisibilityIndex(product.visibility)
                    visibilitySpinner.setSelection(visibilityIndex)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@ProductEditActivity, "Failed to load product details.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCategoryIndex(category: String): Int {
        return categories.indexOf(category)
    }

    private fun getSellRentIndex(sellRent: String): Int {
        return sellRentOptions.indexOf(sellRent)
    }

    private fun getVisibilityIndex(visibility: Boolean): Int {
        return if (visibility) 0 else 1
    }

    private fun updateProductDetails(productId: String) {
        val productsRef = database.reference.child("products").child(productId)

        val productName = productNameEditText.text.toString()
        val category = categorySpinner.selectedItem.toString()
        val productAge = productAgeEditText.text.toString()
        val price = priceEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val sellRent = sellRentSpinner.selectedItem.toString()
        val contactName = contactNameEditText.text.toString()
        val contactEmail = contactEmailEditText.text.toString()
        val contactPhone = contactPhoneEditText.text.toString()
        val contactWay = contactWayEditText.text.toString()
        val location = locationEditText.text.toString()
        val visibility = visibilitySpinner.selectedItemPosition == 0

        // Update product details in Firebase
        val productUpdates = mapOf<String, Any>(
            "productName" to productName,
            "category" to category,
            "productAge" to productAge,
            "price" to price,
            "description" to description,
            "sellRent" to sellRent,
            "contactName" to contactName,
            "contactEmail" to contactEmail,
            "contactPhone" to contactPhone,
            "contactWay" to contactWay,
            "location" to location,
            "visibility" to visibility
            // Add other fields similarly if you have more
        )

        productsRef.updateChildren(productUpdates)
            .addOnSuccessListener {
                Toast.makeText(this@ProductEditActivity, "Product details updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@ProductEditActivity, "Failed to update product details.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setEditMode(isEdit: Boolean) {
        isEditMode = isEdit

        // Enable/disable editing of UI elements
        productNameEditText.isEnabled = isEdit
        categorySpinner.isEnabled = isEdit
        productAgeEditText.isEnabled = isEdit
        priceEditText.isEnabled = isEdit
        descriptionEditText.isEnabled = isEdit
        sellRentSpinner.isEnabled = isEdit
        contactNameEditText.isEnabled = isEdit
        contactEmailEditText.isEnabled = isEdit
        contactPhoneEditText.isEnabled = isEdit
        contactWayEditText.isEnabled = isEdit
        locationEditText.isEnabled = isEdit
        visibilitySpinner.isEnabled = isEdit

        // Change button text based on edit mode
        editSaveButton.text = if (isEdit) "Save" else "Edit"
    }
}
