package com.example.swapseeker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swapseeker.Adapter.ProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GameActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.gameRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns

        // Initialize product list and adapter
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList, ProductAdapter.VIEW_TYPE_ALL)
        recyclerView.adapter = productAdapter

        // Fetch products from Firebase based on category
        fetchProductsFromFirebase("Gaming")
    }

    private fun fetchProductsFromFirebase(category: String) {
        // Add a listener to retrieve data from Firebase Realtime Database based on category
        databaseReference.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Clear existing product list
                    productList.clear()

                    // Iterate through each product in Firebase Realtime Database
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }

                    // Notify the adapter that the data has changed
                    productAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    // You can add your own error handling logic here
                }
            })
    }
}
