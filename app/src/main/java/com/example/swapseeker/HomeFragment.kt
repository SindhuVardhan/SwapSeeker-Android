package com.example.swapseeker


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swapseeker.Adapter.ProductAdapter
import com.example.swapseeker.Category.AutomobileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var responseTimeTextView: TextView
    private lateinit var userId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        // Initialize RecyclerView with GridLayoutManager
        recyclerView = view.findViewById(R.id.allProductsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        // Initialize product list and adapter
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList, ProductAdapter.VIEW_TYPE_ALL, this)
        recyclerView.adapter = productAdapter


        // Fetch products from Firebase
        fetchProductsFromFirebase()

        // Set click listeners for category CardViews
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
        view.findViewById<CardView>(R.id.property).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), PropertyActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.cars).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), AutomobileActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }
        view.findViewById<CardView>(R.id.cameras).setOnClickListener {
            // Open activity for phones category
            val intent = Intent(requireContext(), CameraActivity::class.java)
            // Pass any necessary data using extras
            startActivity(intent)
        }

//        val responseTimeTextView = view.findViewById<TextView>(R.id.responseTimeTextView)
//        val startTime = System.nanoTime() // Start time
//        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val endTime = System.nanoTime() // End time
//                val responseTime = (endTime - startTime) / 1_000_000f // Calculate response time in milliseconds (convert nanoseconds to milliseconds)
//                responseTimeTextView.text = "Response Time: ${String.format("%.2f", responseTime)} ms" // Display response time
//                responseTimeTextView.visibility = View.VISIBLE // Make TextView visible
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
//                // You can add your own error handling logic here
//            }
//        })

        return view
    }

    private fun fetchProductsFromFirebase() {
        // Add a listener to retrieve data from Firebase Realtime Database
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing product list
                productList.clear()

                // Iterate through each product in Firebase Realtime Database
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null && product.visibility) { // Check if visibility is true
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

    private fun calculateDataConsumption() {
        val startTime = System.nanoTime()
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataSize = snapshot.children.sumBy { it.getValue(String::class.java)?.length ?: 0 }
                val endTime = System.nanoTime()
                val dataConsumption = dataSize.toLong()
                val dataConsumptionTextView = view?.findViewById<TextView>(R.id.dataConsumptionTextView)
                dataConsumptionTextView?.text = "Data Consumption: $dataConsumption bytes"
                dataConsumptionTextView?.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun navigateToCategoryActivity(activityClass: Class<*>) {
        // Open activity for the selected category
        val intent = Intent(requireContext(), activityClass)
        startActivity(intent)
    }

    override fun onItemClick(product: Product) {
        // Handle item click, navigate to ProductDetailsActivity
        val intent = Intent(requireContext(), ProductDetailsActivity::class.java)
        intent.putExtra("productId", product.productId)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}
