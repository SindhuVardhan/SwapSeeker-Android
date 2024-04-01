package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.swapseeker.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawerLayout: DrawerLayout? = null
    private var binding: ActivityMainBinding? = null
    private var isLoggedIn = false // Added variable to track login state


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val user = FirebaseAuth.getInstance().currentUser
        Log.d("AuthenticationState", "Current user: $user")


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()

        // Display the HomeFragment when the activity is created
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment()).commit()
        navigationView.setCheckedItem(R.id.home)

        binding!!.bottomNavigationView.background = null
        binding!!.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> navigateToFragment(HomeFragment())
                R.id.shop -> navigateToFragment(shopFragment())
                R.id.add -> navigateToFragment(AddFragment())
                R.id.rent -> navigateToFragment(RentFragment())
                R.id.set -> navigateToFragment(setFragment())
            }
            true
        }

        // Get a reference to the logout menu item
        val logoutMenuItem = navigationView.menu.findItem(R.id.nav_logout)

        // Update the login/logout text based on the user's login state
        logoutMenuItem.title = if (isLoggedIn) "Logout" else "Login"
    }


    private fun showHamburgerIcon() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false) // Hide the back button
            actionBar.setHomeButtonEnabled(true) // Enable the hamburger icon
            drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) // Enable drawer
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }

    private fun navigateToFragment(fragment: Fragment) {
        replaceFragment(fragment)
        showHamburgerIcon()
    }



    private fun handleLoginLogoutClick() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu

        if (isLoggedIn) {
            // User is logged in, perform logout logic
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
            // TODO: Add logout logic, clear user session, etc.
            isLoggedIn = false // Update login state

            // Update the navigation drawer menu items
            menu.findItem(R.id.profile).isVisible = false
            menu.findItem(R.id.my_products).isVisible = false
            menu.findItem(R.id.orders).isVisible = false
            menu.findItem(R.id.notification).isVisible = false
            menu.findItem(R.id.setting).isVisible = false
            menu.findItem(R.id.nav_logout).isVisible = false

            // Pass user information to profileFragment
            val profileFragment = profileFragment()
            val bundle = Bundle().apply {
                putString("name", "name")  // Replace with actual user name
                putString("email", "email")  // Replace with actual user email
                putString("phone", "phone")  // Replace with actual user phone
            }
            profileFragment.arguments = bundle

            // Navigate to profileFragment
            navigateToFragment(profileFragment)
        } else {
            // User is not logged in, navigate to LoginActivity
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> navigateToFragment(profileFragment())
            R.id.my_products -> navigateToFragment(My_productFragment())
            R.id.orders -> navigateToFragment(ordersFragment())
            R.id.notification -> navigateToFragment(notificationFragment())
            R.id.setting -> navigateToFragment(settingFragment())
            R.id.nav_logout -> handleLoginLogoutClick()
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            R.id.my_products -> Toast.makeText(this, "My Products clicked", Toast.LENGTH_SHORT).show()
            R.id.orders -> Toast.makeText(this, "Orders clicked", Toast.LENGTH_SHORT).show()
            R.id.notification -> Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show()
            android.R.id.home -> onBackPressed() // Handle the back button click
        }
        return super.onOptionsItemSelected(item)
    }
}
