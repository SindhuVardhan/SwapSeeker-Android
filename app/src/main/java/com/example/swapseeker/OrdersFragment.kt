package com.example.swapseeker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersFragment : Fragment() {
    // Initialize Firebase components
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var ordersRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("orders")

        // Retrieve the current user's ID
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            // Fetch and display orders for the current user
            fetchAndDisplayOrders(view)
        } else {
            // Handle the case where no user is signed in
        }

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    private fun fetchAndDisplayOrders(view: View) {
        // Query Firebase database to retrieve orders for the current user
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Initialize a list to hold orders
                val ordersList = mutableListOf<Order>()

                // Iterate through each order in the dataSnapshot
                for (orderSnapshot in dataSnapshot.children) {
                    // Check if the order belongs to the current user
                    if (orderSnapshot.child("myOrderId").getValue(String::class.java) == userId) {
                        // Convert the order data to an Order object
                        val order = orderSnapshot.getValue(Order::class.java)
                        // Add the order to the list if it belongs to the current user
                        if (order != null) {
                            ordersList.add(order)
                        }
                    }
                }

                // Set up RecyclerView adapter with ordersList
                val adapter = OrdersAdapter(ordersList)
                ordersRecyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    // Adapter for RecyclerView
    inner class OrdersAdapter(private val ordersList: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val orderImageView: ImageView = itemView.findViewById(R.id.orderImageView)
            val orderNameTextView: TextView = itemView.findViewById(R.id.orderNameTextView)
            val orderPriceTextView: TextView = itemView.findViewById(R.id.orderPriceTextView)
            val orderDateTextView: TextView = itemView.findViewById(R.id.orderDateTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val currentOrder = ordersList[position]

            // Load order image using Glide (you need to implement this)
            Glide.with(holder.itemView.context)
                .load(currentOrder.productImage)
                .placeholder(R.drawable.placeholder1)
                .into(holder.orderImageView)

            holder.orderNameTextView.text = currentOrder.productName
            holder.orderPriceTextView.text = "Price: ₹${currentOrder.price}"
            holder.orderDateTextView.text = "Purchase Date: ${currentOrder.purchaseDate}"
        }

        override fun getItemCount(): Int {
            return ordersList.size
        }
    }
}
