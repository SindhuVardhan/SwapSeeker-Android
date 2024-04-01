package com.example.swapseeker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swapseeker.Adapter.ProductAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class shopFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        // Initialize RecyclerView with GridLayoutManager
        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        // Initialize product list and adapter
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList, ProductAdapter.VIEW_TYPE_SELL)
        recyclerView.adapter = productAdapter



        // Set adapter to RecyclerView
        recyclerView.adapter = productAdapter

        // Fetch products from Firebase
        fetchProductsFromFirebase()



        view.findViewById<CardView>(R.id.phones).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), PhoneActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.laptops).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), LaptopActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.old).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), OldActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.electronics).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), ElectronicActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.games).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), GameActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.cosmetics).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), CosmeticsActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.fashion).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), FashionActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }

//        view.findViewById<View>(R.id.laptops).setOnClickListener {
//            // Handle click for "Laptops" category
//            // Example: Start LaptopsActivity
//            startActivity(Intent(requireContext(), LaptopsActivity::class.java))
//        }

        // Add click listeners for other categories similarly

        return view
    }

    private fun fetchProductsFromFirebase() {
        // Fetch products from Firebase
        // After fetching, set the adapter and click listener
        databaseReference.orderByChild("sellRent").equalTo("Sell")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Clear existing product list
                    productList.clear()

                    // Iterate through each product in Firebase Realtime Database
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        // Check if product is not null and visibility is true
                        if (product != null && product.visibility) {
                            productList.add(product)
                        }
                    }

                    // Set up the adapter
                    productAdapter.notifyDataSetChanged()

                    // Set item click listener
                    productAdapter.setOnItemClickListener(this@shopFragment)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    override fun onItemClick(product: Product) {
        // Handle item click, navigate to ProductDetailsActivity
        val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
        intent.putExtra("productId", product.productId)
        startActivity(intent)
    }
}
