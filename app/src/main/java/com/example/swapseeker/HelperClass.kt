package com.example.swapseeker

class HelperClass {
    var name: String? = null
    var email: String? = null
    var phone: String? = null
    var password: String? = null
    var confirmPassword: String? = null

    constructor(name: String?, email: String?, phone: String?, password: String?, confirm_password: String?) {
        this.name = name
        this.email = email
        this.phone = phone
        this.password = password
        this.confirmPassword = confirm_password
    }

    constructor()
}
