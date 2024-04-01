package com.example.swapseeker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.swapseeker.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.loginbutton.setOnClickListener {
            val loginEmail = binding.email.text.toString()
            val loginPassword = binding.password.text.toString()


            if(loginEmail.isNotEmpty() && loginPassword.isNotEmpty()){
                loginUser(loginEmail,loginPassword)
            } else{
                Toast.makeText(this@LoginActivity, "All Fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirectText.setOnClickListener {
            Toast.makeText(this@LoginActivity, "", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }

    }



    private fun loginUser(email: String,password: String){
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for(userSnapshot in dataSnapshot.children){
                        val userData = userSnapshot.getValue(UserData::class.java)

                        if(userData != null && userData.password == password){
                            Toast.makeText( this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()

                            return
                        }
                    }
                }
                Toast.makeText( this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()

            }
        })

    }

}







