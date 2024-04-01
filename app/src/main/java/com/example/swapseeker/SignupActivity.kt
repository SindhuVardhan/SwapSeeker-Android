package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swapseeker.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()
            val signupPhone = binding.SignupPhone.text.toString()
            val signupEmail = binding.signupEmail.text.toString()

            if(signupUsername.isNotEmpty() && signupPassword.isNotEmpty() && signupPhone.isNotEmpty() && signupEmail.isNotEmpty()){
                createUserWithEmailAndPassword(signupEmail, signupPassword, signupUsername, signupPhone)
            } else {
                showToast("All Fields are required")
            }
        }

        binding.signupRedirectText.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@SignupActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun createUserWithEmailAndPassword(email: String, password: String, username: String, phone: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        // User created successfully, proceed to store additional user data in Firebase Realtime Database
                        val userId = user.uid
                        val userData = UserData(userId, username, password, phone, email)
                        databaseReference.child(userId).setValue(userData)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    showToast("Signup Successful")
                                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                                    finish()
                                } else {
                                    showToast("Failed to store user data: ${dbTask.exception?.message}")
                                }
                            }
                    } else {
                        showToast("User is null")
                    }
                } else {
                    showToast("Signup Failed: ${task.exception?.message}")
                }
            }
    }
}
