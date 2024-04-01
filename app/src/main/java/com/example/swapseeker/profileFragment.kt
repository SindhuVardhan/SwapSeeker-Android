package com.example.swapseeker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.swapseeker.databinding.FragmentProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class profileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var userId: String? = null
    private var isEditMode: Boolean = false

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                uploadProfileImage(imageUri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")
        storageReference = FirebaseStorage.getInstance().reference.child("profile_images")

        // Get the user ID from the arguments or intent
        userId = arguments?.getString("userId") ?: activity?.intent?.getStringExtra("userId")

        if (userId != null) {
            fetchUserData(userId!!)
        }

        binding.editSaveButton.setOnClickListener {
            if (isEditMode) {
                // Save changes
                saveChanges()
            } else {
                // Enter edit mode
                enterEditMode()
            }
        }

        binding.profileImageView.setOnClickListener {
            if (isEditMode) {
                openGallery()
            }
        }
    }

    private fun enterEditMode() {
        isEditMode = true
        binding.editSaveButton.text = "Save"
        // Enable editing of text fields
        binding.textViewUsername.isEnabled = true
        binding.textViewEmail.isEnabled = true
        binding.textViewPhone.isEnabled = true
    }

    private fun saveChanges() {
        isEditMode = false
        binding.editSaveButton.text = "Edit"
        // Disable editing of text fields
        binding.textViewUsername.isEnabled = false
        binding.textViewEmail.isEnabled = false
        binding.textViewPhone.isEnabled = false

        // Update user data in Firebase
        val userReference = userId?.let { databaseReference.child(it) }
        val newUsername = binding.textViewUsername.text.toString()
        val newEmail = binding.textViewEmail.text.toString()
        val newPhone = binding.textViewPhone.text.toString()
        if (userReference != null) {
            userReference.child("username").setValue(newUsername)
        }
        if (userReference != null) {
            userReference.child("email").setValue(newEmail)
        }
        if (userReference != null) {
            userReference.child("phone").setValue(newPhone)
        }

        Toast.makeText(requireContext(), "Changes saved", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        if (imageUri != null) {
            val profileImageRef = storageReference.child("$userId/profile.jpg")
            profileImageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        updateProfileImage(imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProfileImage(imageUrl: String) {
        val userReference = userId?.let { databaseReference.child(it) }
        if (userReference != null) {
            userReference.child("imageUrl").setValue(imageUrl)
                .addOnSuccessListener {
                    // Update profile image in UI
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.profileImageView)
                    Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update profile image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchUserData(userId: String) {
        val userReference = databaseReference.child(userId)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val UserData = dataSnapshot.getValue(UserData::class.java)

                    if (UserData != null) {
                        // Update UI elements with user data
                        binding.textViewUsername.text = UserData.username
                        binding.textViewEmail.text = UserData.email
                        binding.textViewPhone.text = UserData.phone

                        // Load profile image using Picasso
                        if (!UserData.imageUrl.isNullOrEmpty()) {
                            Picasso.get()
                                .load(UserData.imageUrl)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(binding.profileImageView)
                        } else {
                            // Set default image if imageUrl is empty or null
                            binding.profileImageView.setImageResource(R.drawable.ic_launcher_foreground)
                        }
                    }
                } else {
                    // User data not found
                    Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(requireContext(), "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
