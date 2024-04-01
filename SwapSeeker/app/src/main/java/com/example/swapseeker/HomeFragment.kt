package com.example.swapseeker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var productList: List<Product>
    private lateinit var requestQueue: RequestQueue

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productList = ArrayList()

        requestQueue = Volley.newRequestQueue(requireActivity())
        fetchProducts()

        return view
    }

    private fun fetchProducts() {
        val url = "http://172.20.10.5/swapseeker/api/v2/display_api.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val products = mutableListOf<Product>()

                    for (i in 0 until response.length()) {
                        val productObject = response.getJSONObject(i)

//                        val product = Product(
//                            productObject.getString("product_id"),
//                            productObject.getString("product_name"),
//                            productObject.getString("category"),
//                            productObject.getString("product_age"),
//                            productObject.getString("price"),
//                            productObject.getString("description"),
//                            productObject.getString("image_name"),
//                            productObject.getString("name"),
//                            productObject.getString("email"),
//                            productObject.getString("phone"),
//                            productObject.getString("contact_way"),
//                            productObject.getString("location"),
//                            productObject.getString("sell_rent")
//
//                        )
//
//                        products.add(product)
                    }

                    productList = products

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                // Handle error
            }
        )

        requestQueue.add(jsonArrayRequest)
    }
}
