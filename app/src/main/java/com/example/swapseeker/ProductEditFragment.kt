package com.example.swapseeker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProductEditFragment : Fragment() {
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
    private lateinit var saveButton: Button

    private lateinit var productId: String // Product ID passed from previous fragment
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_edit, container, false)

        // Initialize Firebase
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        productNameEditText = view.findViewById(R.id.product_name)
        categorySpinner = view.findViewById(R.id.category)
        productAgeEditText = view.findViewById(R.id.product_age)
        priceEditText = view.findViewById(R.id.price)
        descriptionEditText = view.findViewById(R.id.description)
        sellRentSpinner = view.findViewById(R.id.sell_rent)
        contactNameEditText = view.findViewById(R.id.contactName)
        contactEmailEditText = view.findViewById(R.id.contactEmail)
        contactPhoneEditText = view.findViewById(R.id.contactPhone)
        contactWayEditText = view.findViewById(R.id.contactWay)
        locationEditText = view.findViewById(R.id.location)
        saveButton = view.findViewById(R.id.postProductButton)

        // Retrieve product ID from arguments
        productId = arguments?.getString("productId") ?: ""

        // Load product details from Firebase
        loadProductDetails(productId)

        // Set up click listener for save button
        saveButton.setOnClickListener {
            // Update product details in Firebase
            updateProductDetails(productId)
        }

        return view
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
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(requireContext(), "Failed to load product details.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCategoryIndex(category: String): Int {
        val categories = resources.getStringArray(R.array.categories_array)
        return categories.indexOf(category)
    }

    private fun getSellRentIndex(sellRent: String): Int {
        val sellRentOptions = resources.getStringArray(R.array.sell_rent_array)
        return sellRentOptions.indexOf(sellRent)
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

        // Update product details in Firebase
        val productUpdates = mapOf<String, Any>(
            "name" to productName,
            "category" to category,
            "age" to productAge,
            "price" to price,
            "description" to description,
            "sellRent" to sellRent,
            "contactName" to contactName,
            "contactEmail" to contactEmail,
            "contactPhone" to contactPhone,
            "contactWay" to contactWay,
            "location" to location
            // Add other fields similarly if you have more
        )

        productsRef.updateChildren(productUpdates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Product details updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to update product details.", Toast.LENGTH_SHORT).show()
            }
    }
}
