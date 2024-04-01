package com.example.swapseeker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class AddFragment : Fragment() {

    private lateinit var productName: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var productAge: EditText
    private lateinit var price: EditText
    private lateinit var description: EditText
    private lateinit var sellRentSpinner: Spinner
    private lateinit var contactName: EditText
    private lateinit var contactEmail: EditText
    private lateinit var contactPhone: EditText
    private lateinit var contactWay: EditText
    private lateinit var location: EditText
    private lateinit var img1: ImageView
    private lateinit var img2: ImageView
    private lateinit var img3: ImageView
    private lateinit var img4: ImageView
    private lateinit var postProductButton: Button
    private var selectedImageView: ImageView? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var storageReference: StorageReference

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // Initialize views
        productName = view.findViewById(R.id.product_name)
        categorySpinner = view.findViewById(R.id.category)
        productAge = view.findViewById(R.id.product_age)
        price = view.findViewById(R.id.price)
        description = view.findViewById(R.id.description)
        sellRentSpinner = view.findViewById(R.id.sell_rent)
        contactName = view.findViewById(R.id.contactName)
        contactEmail = view.findViewById(R.id.contactEmail)
        contactPhone = view.findViewById(R.id.contactPhone)
        contactWay = view.findViewById(R.id.contactWay)
        location = view.findViewById(R.id.location)
        img1 = view.findViewById(R.id.img1)
        img2 = view.findViewById(R.id.img2)
        img3 = view.findViewById(R.id.img3)
        img4 = view.findViewById(R.id.img4)
        postProductButton = view.findViewById(R.id.postProductButton)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // Set up category spinner
        val categories = arrayOf(
            "SmartPhones", "Toys",
            "Property", "Old_is_Gold", "HomeAppliances", "Gaming",
            "Fashion", "Electronics", "Cosmetics",
            "Computers", "Cameras", "AutoMobiles"
        )
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Set up sell/rent spinner
        val sellRentOptions = arrayOf("Sell", "Rent")
        val sellRentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sellRentOptions)
        sellRentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sellRentSpinner.adapter = sellRentAdapter

        // Set onClick listeners for image views
        img1.setOnClickListener { selectImage(img1) }
        img2.setOnClickListener { selectImage(img2) }
        img3.setOnClickListener { selectImage(img3) }
        img4.setOnClickListener { selectImage(img4) }

        // Set onClick listener for post product button
        postProductButton.setOnClickListener { postProduct() }
        val user = firebaseAuth.currentUser
        Log.d("AuthenticationState", "Current user: $user")

        return view
    }

    private fun selectImage(imageView: ImageView) {
        selectedImageView = imageView  // Updated this line

        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Action")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePhoto()
                1 -> chooseFromGallery()
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageView?.setImageURI(selectedImageUri)  // Updated this line
        }
    }

    private fun postProduct() {
        // Check if user is authenticated
        val user = firebaseAuth.currentUser
        Log.d("AuthenticationState", "Current user: $user")
        if (user == null) {
            // User not authenticated, handle accordingly
            // For example, you can redirect to the login screen
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)
            requireActivity().finish() // Optional: Close the current activity if needed

            // You can also show a message to inform the user
            Toast.makeText(requireContext(), "Please log in to post a product", Toast.LENGTH_SHORT).show()

            // Return from the function since the user is not authenticated
            return
        }

        // Create a unique product ID
        val productId = UUID.randomUUID().toString()

        // Get user ID
        val userId = user.uid

        // Get other product details
        val productNameValue = productName.text.toString()
        val categoryValue = categorySpinner.selectedItem.toString()
        val productAgeValue = productAge.text.toString()
        val priceValue = price.text.toString()
        val descriptionValue = description.text.toString()
        val sellRentValue = sellRentSpinner.selectedItem.toString()
        val contactNameValue = contactName.text.toString()
        val contactEmailValue = contactEmail.text.toString()
        val contactPhoneValue = contactPhone.text.toString()
        val contactWayValue = contactWay.text.toString()
        val locationValue = location.text.toString()

        // Validate product details (add your validation logic here)

        // Upload image to Firebase Storage
        if (selectedImageUri != null) {
            val imageRef = storageReference.child("product_images/$productId.jpg")
            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        // Create Product object
                        val product = Product(
                            productId,
                            userId,
                            productNameValue,
                            categoryValue,
                            productAgeValue,
                            priceValue,
                            descriptionValue,
                            sellRentValue,
                            contactNameValue,
                            contactEmailValue,
                            contactPhoneValue,
                            contactWayValue,
                            locationValue,
                            imageUrl
                        )

                        // Save product to Firebase Realtime Database
                        val productRef = firebaseDatabase.reference.child("products").child(productId)
                        productRef.setValue(product)
                            .addOnSuccessListener {
                                // Product details successfully saved
                                Toast.makeText(requireContext(), "Product details saved", Toast.LENGTH_SHORT).show()

                                // Navigate to HomeFragment
                                val intent = Intent(requireContext(), HomeFragment::class.java)
                                startActivity(intent)
                                requireActivity().finish() // Optional: Close the current activity if needed
                            }
                            .addOnFailureListener { e ->
                                // Handle the error
                                Toast.makeText(requireContext(), "Failed to save product details", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No image selected, handle accordingly
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }
}
