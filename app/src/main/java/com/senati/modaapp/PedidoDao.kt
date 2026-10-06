package com.senati.modaapp

import android.content.ContentValues
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun registrarPedido(items: List<ItemCarrito>, total: Double, idUsuario: Int = 1): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()

        return try {
            val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val valuesPedido = ContentValues().apply {
                put("id_usuario", idUsuario)
                put("fecha", fechaActual)
                put("total", total)
                put("estado", "Pendiente")
            }

            val idPedido = db.insert("pedido", null, valuesPedido)

            if (idPedido == -1L) {
                return false
            }

            // Insertar el detalle de cada prenda en el pedido
            for (item in items) {
                val valuesDetalle = ContentValues().apply {
                    put("id_pedido", idPedido)
                    put("id_ropa", item.ropa.id)
                    put("cantidad", item.cantidad)
                    put("precio_unitario", item.ropa.precio)
                }
                db.insert("detalle_pedido", null, valuesDetalle)
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}