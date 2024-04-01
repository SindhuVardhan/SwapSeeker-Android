package com.example.swapseeker

data class Address(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address_1: String? = null,
    val address_2: String? = null,
    val country: String? = null,
    val city: String? = null,
    val state: String? = null,
    val myAddress: String? = null
) {
    // No-argument constructor required for Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "")
}

