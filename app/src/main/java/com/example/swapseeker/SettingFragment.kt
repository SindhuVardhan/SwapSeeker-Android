package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingFragment : Fragment() {

    private lateinit var logoutTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        logoutTextView = view.findViewById(R.id.logoutTextView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for logoutTextView
        logoutTextView.setOnClickListener {
            // Handle logout logic here
            // For simplicity, let's assume the user is always logged in
            logoutUser()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        // Perform logout actions here
        showToast("Logout successful")

        // Example: Navigate to LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Finish the current activity
    }
}
