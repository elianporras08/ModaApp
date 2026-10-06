package com.senati.modaapp

data class ItemCarrito(
    val ropa: Ropa,
    var cantidad: Int = 1
) {
    val subtotal: Double
        get() = ropa.precio * cantidad
}