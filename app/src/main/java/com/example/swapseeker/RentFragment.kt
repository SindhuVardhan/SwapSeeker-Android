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
import com.example.swapseeker.Category.AutomobileActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RentFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rent, container, false)

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        // Initialize RecyclerView with GridLayoutManager
        recyclerView = view.findViewById(R.id.recyclerViewRentProducts)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        // Initialize product list and adapter
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList, ProductAdapter.VIEW_TYPE_RENT)
        recyclerView.adapter = productAdapter

        // Fetch products from Firebase
        fetchProductsFromFirebase()

        // Set click listeners for other categories
        view.findViewById<CardView>(R.id.old).setOnClickListener {
            startActivity(Intent(requireContext(), OldActivity::class.java))
        }
        view.findViewById<CardView>(R.id.electronics).setOnClickListener {
            startActivity(Intent(requireContext(), ElectronicActivity::class.java))
        }
        view.findViewById<CardView>(R.id.games).setOnClickListener {
            startActivity(Intent(requireContext(), GameActivity::class.java))
        }
        view.findViewById<CardView>(R.id.property).setOnClickListener {
            startActivity(Intent(requireContext(), PropertyActivity::class.java))
        }
        view.findViewById<CardView>(R.id.cars).setOnClickListener {
            startActivity(Intent(requireContext(), AutomobileActivity::class.java))
        }
        view.findViewById<CardView>(R.id.cameras).setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }

        return view
    }

    private fun fetchProductsFromFirebase() {
        // Fetch products from Firebase where visibility is true and sellRent is "Rent"
        databaseReference.orderByChild("sellRent").equalTo("Rent")
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

                    // Set item click listener for RentFragment
                    productAdapter.setOnRentItemClickListener(this@RentFragment)
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
