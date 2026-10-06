package com.senati.modaapp

import android.content.ContentValues
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PedidoItem(
    val id: Int,
    val idUsuario: Int,
    val fecha: String,
    val total: Double,
    val estado: String
)

data class ClienteItem(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val totalPedidos: Int
)

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
                put("estado", "PENDIENTE")
            }

            val idPedido = db.insert("pedido", null, valuesPedido)

            if (idPedido == -1L) {
                return false
            }

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

    fun listarPorEstado(estado: String): List<PedidoItem> {
        val lista = mutableListOf<PedidoItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM pedido WHERE estado = ? ORDER BY id DESC",
            arrayOf(estado)
        )

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    PedidoItem(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                        total = cursor.getDouble(cursor.getColumnIndexOrThrow("total")),
                        estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun atenderPedido(idPedido: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()

        return try {
            val cursorDetalle = db.rawQuery(
                "SELECT id_ropa, cantidad FROM detalle_pedido WHERE id_pedido = ?",
                arrayOf(idPedido.toString())
            )

            if (cursorDetalle.moveToFirst()) {
                do {
                    val idRopa = cursorDetalle.getInt(cursorDetalle.getColumnIndexOrThrow("id_ropa"))
                    val cantComprada = cursorDetalle.getInt(cursorDetalle.getColumnIndexOrThrow("cantidad"))

                    db.execSQL(
                        "UPDATE ropa SET cantidad = cantidad - ? WHERE id = ? AND cantidad >= ?",
                        arrayOf(cantComprada, idRopa, cantComprada)
                    )
                } while (cursorDetalle.moveToNext())
            }
            cursorDetalle.close()

            val values = ContentValues().apply {
                put("estado", "ATENDIDO")
            }
            val actualizados = db.update("pedido", values, "id = ?", arrayOf(idPedido.toString()))

            if (actualizados > 0) {
                db.setTransactionSuccessful()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun obtenerTotalVentasAtendidas(): Double {
        var total = 0.0
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT SUM(total) FROM pedido WHERE estado = 'ATENDIDO'", null)
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun obtenerCantidadPedidosAtendidos(): Int {
        var cantidad = 0
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM pedido WHERE estado = 'ATENDIDO'", null)
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return cantidad
    }

    // Obtener directorio de usuarios/clientes con el conteo de sus pedidos (HU-12, CA3)
    fun obtenerClientesConPedidos(): List<ClienteItem> {
        val lista = mutableListOf<ClienteItem>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT u.id, u.usuario, u.telefono, COUNT(p.id) AS total_pedidos
            FROM usuario u
            LEFT JOIN pedido p ON u.id = p.id_usuario
            GROUP BY u.id, u.usuario, u.telefono
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("usuario"))
                val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")) ?: "987654321"
                val totalPedidos = cursor.getInt(cursor.getColumnIndexOrThrow("total_pedidos"))

                lista.add(ClienteItem(id, nombre, telefono, totalPedidos))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}