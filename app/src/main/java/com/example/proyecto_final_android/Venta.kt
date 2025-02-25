package com.example.proyecto_final_android

data class Venta(
    val number_sale: String,
    val dni: String,
    val date: String,
    val client: String,
    val product: Producto,
    val quantity: Int,
    val total: Double,
    val discount: Boolean
)