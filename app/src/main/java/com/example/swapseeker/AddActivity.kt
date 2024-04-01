package com.example.swapseeker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class AddActivity : AppCompatActivity() {

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

    // Global variables to hold byte array data for each image
    private var data1: ByteArray? = null
    private var data2: ByteArray? = null
    private var data3: ByteArray? = null
    private var data4: ByteArray? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // Initialize views
        productName = findViewById(R.id.product_name)
        categorySpinner = findViewById(R.id.category)
        productAge = findViewById(R.id.product_age)
        price = findViewById(R.id.price)
        description = findViewById(R.id.description)
        sellRentSpinner = findViewById(R.id.sell_rent)
        contactName = findViewById(R.id.contactName)
        contactEmail = findViewById(R.id.contactEmail)
        contactPhone = findViewById(R.id.contactPhone)
        contactWay = findViewById(R.id.contactWay)
        location = findViewById(R.id.location)
        img1 = findViewById(R.id.img1)
        img2 = findViewById(R.id.img2)
        img3 = findViewById(R.id.img3)
        img4 = findViewById(R.id.img4)
        postProductButton = findViewById(R.id.postProductButton)

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
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Set up sell/rent spinner
        val sellRentOptions = arrayOf("Sell", "Rent")
        val sellRentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sellRentOptions)
        sellRentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sellRentSpinner.adapter = sellRentAdapter

        // Set onClick listeners for image views
        img1.setOnClickListener { selectImage(img1) }
        img2.setOnClickListener { selectImage(img2) }
        img3.setOnClickListener { selectImage(img3) }
        img4.setOnClickListener { selectImage(img4) }

        // Set onClick listener for post product button
        postProductButton.setOnClickListener { postProduct() }
    }

    private fun selectImage(imageView: ImageView) {
        selectedImageView = imageView

        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.data?.let { uri ->
                        // Decode the selected image URI to byte array
                        val bitmap = decodeUri(uri, 800)
                        val outputStream = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        val imageBytes = outputStream.toByteArray()

                        // Determine which image view was clicked and assign the corresponding byte array
                        when (selectedImageView) {
                            img1 -> data1 = imageBytes
                            img2 -> data2 = imageBytes
                            img3 -> data3 = imageBytes
                            img4 -> data4 = imageBytes
                        }

                        // Display the selected image in the ImageView
                        selectedImageView?.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun decodeUri(uri: Uri, requiredSize: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
        options.inSampleSize = calculateInSampleSize(options, requiredSize, requiredSize)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun postProduct() {
        // Create a unique product ID (numerical)
        val productId = System.currentTimeMillis().toString()

        // Get the current user ID
        val userId = firebaseAuth.currentUser?.uid ?: ""

        // Set myProductId to userId
        val myProductId = userId

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

        // Upload images to Firebase Storage and save product to Firebase Realtime Database
        val image1Ref = storageReference.child("product_images/$productId/1.jpg")
        val image2Ref = storageReference.child("product_images/$productId/2.jpg")
        val image3Ref = storageReference.child("product_images/$productId/3.jpg")
        val image4Ref = storageReference.child("product_images/$productId/4.jpg")

        // Upload image1
        image1Ref.putBytes(data1 ?: ByteArray(0))
            .addOnSuccessListener { _ ->
                // Get the download URL for image1
                image1Ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Upload image2
                    image2Ref.putBytes(data2 ?: ByteArray(0))
                        .addOnSuccessListener { _ ->
                            // Get the download URL for image2
                            image2Ref.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl2 = uri.toString()

                                // Upload image3
                                image3Ref.putBytes(data3 ?: ByteArray(0))
                                    .addOnSuccessListener { _ ->
                                        // Get the download URL for image3
                                        image3Ref.downloadUrl.addOnSuccessListener { uri ->
                                            val imageUrl3 = uri.toString()

                                            // Upload image4
                                            image4Ref.putBytes(data4 ?: ByteArray(0))
                                                .addOnSuccessListener { _ ->
                                                    // Get the download URL for image4
                                                    image4Ref.downloadUrl.addOnSuccessListener { uri ->
                                                        val imageUrl4 = uri.toString()

                                                        // Create Product object
                                                        val product = Product(
                                                            productId,
                                                            userId,
                                                            myProductId,
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
                                                            imageUrl,
                                                            imageUrl2,
                                                            imageUrl3,
                                                            imageUrl4
                                                        )

                                                        // Save product to Firebase Realtime Database
                                                        val productRef = firebaseDatabase.reference.child("products").child(productId)
                                                        productRef.setValue(product)
                                                            .addOnSuccessListener {
                                                                // Product details successfully saved
                                                                Toast.makeText(this, "Product details saved", Toast.LENGTH_SHORT).show()

                                                                // Show success dialog
                                                                showSuccessDialog()
                                                            }
                                                            .addOnFailureListener { e ->
                                                                // Handle the error
                                                                Toast.makeText(this, "Failed to save product details", Toast.LENGTH_SHORT).show()
                                                            }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    // Handle the error
                                                    Toast.makeText(this, "Failed to upload image4", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle the error
                                        Toast.makeText(this, "Failed to upload image3", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            Toast.makeText(this, "Failed to upload image2", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
                Toast.makeText(this, "Failed to upload image1", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage("Product added successfully")

        builder.setPositiveButton("OK") { _, _ ->
            // Navigate to HomeActivity
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
            finish() // Optional: Close the current activity if needed
        }

        val dialog = builder.create()
        dialog.show()
    }
}
