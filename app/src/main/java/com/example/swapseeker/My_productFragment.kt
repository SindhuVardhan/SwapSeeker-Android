package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swapseeker.Adapter.ProductAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class My_productFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_product, container, false)
        recyclerView = view.findViewById(R.id.myProductsRecyclerView)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        fetchUserProducts()
        return view
    }

    private fun fetchUserProducts() {
        val userId = auth.currentUser?.uid
        val productsRef = database.reference.child("products")

        val query = productsRef.orderByChild("myproductId").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                // Initialize and set up the RecyclerView adapter
                adapter = ProductAdapter(productList, ProductAdapter.VIEW_TYPE_ALL)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                // Set click listener for RecyclerView items
                adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
                    override fun onItemClick(product: Product) {
                        // Navigate to ProductEditActivity with the selected product ID
                        val intent = Intent(requireContext(), ProductEditActivity::class.java)
                        intent.putExtra("productId", product.productId)
                        startActivity(intent)
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}
