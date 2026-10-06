package com.senati.modaapp

object CarritoManager {
    private val listaItems = mutableListOf<ItemCarrito>()

    fun agregarProducto(ropa: Ropa) {
        val itemExistente = listaItems.find { it.ropa.id == ropa.id }
        if (itemExistente != null) {
            itemExistente.cantidad++
        } else {
            listaItems.add(ItemCarrito(ropa, 1))
        }
    }

    fun obtenerItems(): List<ItemCarrito> = listaItems

    fun obtenerTotal(): Double {
        return listaItems.sumOf { it.subtotal }
    }

    fun obtenerCantidadTotalItems(): Int {
        return listaItems.sumOf { it.cantidad }
    }

    fun eliminarItem(posicion: Int) {
        if (posicion in listaItems.indices) {
            listaItems.removeAt(posicion)
        }
    }

    fun limpiarCarrito() {
        listaItems.clear()
    }
}