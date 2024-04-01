package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    private var isLoggedIn = false
    private var userId: String? = null
    private var username: String? = null
    private var userEmail: String? = null
    private var userImageUrl: String? = null

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val fragmentToLoad = intent.getStringExtra("CART")

        val user = FirebaseAuth.getInstance().currentUser
        userId = user?.uid
        username = user?.displayName
        userEmail = user?.email

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        updateUserProfileInNavigationViewHeader(username, userEmail, null, navigationView)

        // Fetch user data including profile image URL
        fetchUserData(userId)

        setupNavigation()

        if (fragmentToLoad != null) {
            when (fragmentToLoad) {
                "cart" -> {
                    // Load CartFragment
                    navigateToFragment(CartFragment())
                    binding.bottomNavigationView.menu.findItem(R.id.cart)?.isChecked = true
                }
                "order" -> {
                    // Load OrdersFragment
                    navigateToFragment(OrdersFragment())
                    binding.bottomNavigationView.menu.findItem(R.id.orders)?.isChecked = true
                }
                else -> {
                    // Default fragment to display
                    navigateToFragment(HomeFragment())
                }
            }
        } else {
            // Default fragment to display if fragmentToLoad is null
            navigateToFragment(HomeFragment())
        }


    }

    private fun setupNavigation() {
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val homeFragment = HomeFragment().apply {
                        arguments = Bundle().apply {
                            putString("userId", userId) // Pass the userId to the HomeFragment
                        }
                    }
                    navigateToFragment(homeFragment)
                }
                R.id.shop -> navigateToFragment(shopFragment())
                R.id.add -> startAnotherActivity()
                R.id.rent -> navigateToFragment(RentFragment())
//                R.id.cart -> startActivity(Intent(this, CartActivity::class.java))
                R.id.cart -> navigateToFragment(CartFragment())
            }
            true
        }
    }


    private fun showHamburgerIcon() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeButtonEnabled(true)
        }
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun startAnotherActivity() {
        val intent = Intent(this, AddActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }

    private fun navigateToFragment(fragment: Fragment) {
        replaceFragment(fragment)
        showHamburgerIcon()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> navigateToFragment(profileFragment().apply {
                arguments = Bundle().apply { putString("userId", userId) }
            })

            R.id.my_products -> navigateToFragment(My_productFragment().apply {
                arguments = Bundle().apply { putString("userId", userId) }
            })
            R.id.orders -> navigateToFragment(OrdersFragment().apply {
                arguments = Bundle().apply { putString("userId", userId) }
            })

//            R.id.orders -> startActivity(
//                Intent(
//                    this,
//                    OrdersActivity::class.java
//                ).apply { putExtra("userId", userId) })

//            R.id.notification -> navigateToFragment(notificationFragment().apply {
//                arguments = Bundle().apply { putString("userId", userId) }
//            })

            R.id.setting -> navigateToFragment(SettingFragment().apply {
                arguments = Bundle().apply { putString("userId", userId) }
            })

            R.id.nav_logout -> {
                // Log out the user
                FirebaseAuth.getInstance().signOut()

                // Redirect to the LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))

                // Finish the MainActivity to prevent going back to it
                finish()
            }
        }


        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
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
//            R.id.notification -> Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show()
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUserProfileInNavigationViewHeader(username: String?, email: String?, imageUrl: String?, navigationView: NavigationView) {
        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profileImageView)

        usernameTextView.text = username
        emailTextView.text = email

        // Load profile image into profileImageView using Picasso
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.user) // Placeholder image while loading
                .error(R.drawable.user) // Image to display if loading fails
                .into(profileImageView)
        } else {
            // Set default profile image if imageUrl is empty or null
            profileImageView.setImageResource(R.drawable.user)
        }
    }

    private fun fetchUserData(userId: String?) {
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val imageUrl = dataSnapshot.child("imageUrl").getValue(String::class.java)
                        // Update navigation header with profile image URL
                        updateUserProfileInNavigationViewHeader(username, userEmail, imageUrl, binding.navView)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@MainActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
