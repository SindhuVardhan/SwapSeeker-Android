package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swapseeker.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backicons.setOnClickListener {
            val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }



        auth = FirebaseAuth.getInstance()

        binding.loginbutton.setOnClickListener {
            if (validateEmail() && validatePassword()) {
                loginUser()
            }
        }

        binding.signupRedirectText.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userId = user.uid
                        val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                        mainIntent.putExtra("userId", userId)
                        startActivity(mainIntent)
                        finish() // Finish the LoginActivity to prevent going back to it

                        // Start AddActivity and CartActivity with userId as extras
                        val addIntent = Intent(this@LoginActivity, AddActivity::class.java)
                        addIntent.putExtra("userId", userId)


                        val cartIntent = Intent(this@LoginActivity, CartActivity::class.java)
                        cartIntent.putExtra("userId", userId)


                        val addressIntent = Intent(this@LoginActivity, AddressActivity::class.java)
                        addressIntent.putExtra("userId", userId)

                        val detailsIntent = Intent(this@LoginActivity, ProductDetailsActivity::class.java)
                        detailsIntent.putExtra("userId", userId)

                        val checkoutIntent = Intent(this@LoginActivity, CheckoutActivity::class.java)
                        checkoutIntent.putExtra("userId", userId)

                        val ordersIntent = Intent(this@LoginActivity, OrdersActivity::class.java)
                        ordersIntent.putExtra("userId", userId)

                    }
                } else {
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    handleAuthError(errorCode)
                }
            }
    }

    private fun handleAuthError(errorCode: String) {
        when (errorCode) {
            "ERROR_INVALID_EMAIL" -> {
                binding.email.error = "Invalid email address"
                binding.email.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                binding.password.error = "Wrong password"
                binding.password.requestFocus()
            }
            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this@LoginActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.email.text.toString().trim()
        return if (email.isEmpty()) {
            binding.email.error = "Email cannot be empty"
            false
        } else {
            binding.email.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.password.text.toString().trim()
        return if (password.isEmpty()) {
            binding.password.error = "Password cannot be empty"
            false
        } else {
            binding.password.error = null
            true
        }
    }
}
