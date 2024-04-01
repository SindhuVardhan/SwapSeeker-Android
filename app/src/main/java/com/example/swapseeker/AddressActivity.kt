package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddressActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var address1EditText: EditText
    private lateinit var address2EditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var saveAddressButton: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        address1EditText = findViewById(R.id.address1EditText)
        address2EditText = findViewById(R.id.address2EditText)
        countryEditText = findViewById(R.id.countryEditText)
        cityEditText = findViewById(R.id.cityEditText)
        stateEditText = findViewById(R.id.stateEditText)
        saveAddressButton = findViewById(R.id.saveAddressButton)

        // Retrieve the current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if a user is signed in
        if (currentUser != null) {
            // Retrieve the user ID
            userId = currentUser.uid
            // Initialize the Firebase database reference
            databaseReference = FirebaseDatabase.getInstance().reference.child("addresses").child(userId)
        } else {
            // No user is signed in, redirect to login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set click listener for the save button
        saveAddressButton.setOnClickListener {
            saveAddressToFirebase()
        }
    }

    private fun saveAddressToFirebase() {
        // Retrieve address details from EditText fields
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val address1 = address1EditText.text.toString().trim()
        val address2 = address2EditText.text.toString().trim()
        val country = countryEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val state = stateEditText.text.toString().trim()

        // Perform validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address1.isEmpty() || country.isEmpty() || city.isEmpty() || state.isEmpty()) {
            // Display error message or toast to inform the user to fill in all required fields
            return
        }

        // Generate a unique key for the address
        val addressId = databaseReference.push().key ?: ""

        // Create an Address object with the retrieved details
        val address = Address(
            id = addressId,
            name = name,
            email = email,
            phone = phone,
            address_1 = address1,
            address_2 = address2,
            country = country,
            city = city,
            state = state,
            myAddress = userId
        )

        // Save address to Firebase under a unique key
        databaseReference.child(addressId).setValue(address)
            .addOnSuccessListener {
                // Address saved successfully, navigate to CheckoutActivity
                navigateToCheckout(addressId)
            }
            .addOnFailureListener { exception ->
                // Handle error saving address
            }
    }

    private fun navigateToCheckout(addressId: String) {
        val intent = Intent(this@AddressActivity, CheckoutActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("addressId", addressId)
        intent.putExtra("myAddress", userId)
        startActivity(intent)
        finish()
    }
}
