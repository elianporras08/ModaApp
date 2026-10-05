package com.senati.modaapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class RopaDao(context: Context) {
    private val dbHelper = DBHelper(context)

    // Insertar una prenda en la base de datos (HU-05)
    fun insertar(ropa: Ropa): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("modelo", ropa.modelo)
            put("id_categoria", ropa.idCategoria)
            put("talla", ropa.talla)
            put("marca", ropa.marca)
            put("color", ropa.color)
            put("precio", ropa.precio)
            put("cantidad", ropa.cantidad)
            put("foto", ropa.foto)
        }
        val resultado = db.insert("ropa", null, values)
        db.close()
        return resultado != -1L
    }

    // Listar las categorías para los Spinners y Chips (HU-05, HU-06)
    fun listarCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM categoria ORDER BY nombre", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                lista.add(Categoria(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Listar prendas disponibles (cantidad > 0) para el catálogo del cliente (HU-06)
    fun listarDisponibles(idCategoria: Int = 0): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase

        var query = """
            SELECT r.*, c.nombre AS categoria_nombre 
            FROM ropa r 
            INNER JOIN categoria c ON r.id_categoria = c.id 
            WHERE r.cantidad > 0
        """.trimIndent()

        val params = mutableListOf<String>()

        if (idCategoria > 0) {
            query += " AND r.id_categoria = ?"
            params.add(idCategoria.toString())
        }

        query += " ORDER BY r.id DESC"

        val cursor: Cursor = db.rawQuery(query, if (params.isNotEmpty()) params.toTypedArray() else null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
                val idCat = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"))
                val catNombre = cursor.getString(cursor.getColumnIndexOrThrow("categoria_nombre"))
                val talla = cursor.getString(cursor.getColumnIndexOrThrow("talla"))
                val marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"))
                val color = cursor.getString(cursor.getColumnIndexOrThrow("color"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))

                lista.add(Ropa(id, modelo, idCat, catNombre, talla, marca, color, precio, cantidad, foto))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Listar todas las prendas registradas para la lista del administrador (HU-05)
    fun listarTodas(): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT r.*, c.nombre AS categoria_nombre 
            FROM ropa r 
            INNER JOIN categoria c ON r.id_categoria = c.id 
            ORDER BY r.id DESC
        """.trimIndent()

        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
                val idCat = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria"))
                val catNombre = cursor.getString(cursor.getColumnIndexOrThrow("categoria_nombre"))
                val talla = cursor.getString(cursor.getColumnIndexOrThrow("talla"))
                val marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"))
                val color = cursor.getString(cursor.getColumnIndexOrThrow("color"))
                val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))

                lista.add(Ropa(id, modelo, idCat, catNombre, talla, marca, color, precio, cantidad, foto))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}