package com.example.swapseeker
import java.io.Serializable
data class Product(
    val productId: String = "",
    val userId: String = "",
    val myproductId: String = "",
    val productName: String = "",
    val category: String = "",
    val productAge: String = "",
    val price: String = "",
    val description: String = "",
    val sellRent: String = "",
    val contactName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val contactWay: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val imageUrl2: String = "",
    val imageUrl3: String = "",
    val imageUrl4: String = "",
    val visibility: Boolean = true // Add visibility field
): Serializable {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "", "","","","","", true)
}
