package com.example.swapseeker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class profileFragment : Fragment() {
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userName = view.findViewById(R.id.name)
        userEmail = view.findViewById(R.id.email)
        userPhone = view.findViewById(R.id.phone)

        // Retrieve user information from arguments
        val args = arguments
        if (args != null) {
            val name = args.getString("name", "")
            val email = args.getString("email", "")
            val phone = args.getString("phone", "")

            // Set the retrieved information to TextViews
            userName.text = name
            userEmail.text = email
            userPhone.text = phone
        }

        return view
    }
}
