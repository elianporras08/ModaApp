package com.senati.modaapp

import android.content.ContentValues
import android.content.Context

class RopaDao(context: Context) {

    private val dbHelper = DBHelper(context)

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
        val id = db.insert("ropa", null, values)
        db.close()
        return id != -1L
    }

    fun listarCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categoria", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Categoria(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun listarDisponibles(idCategoria: Int = 0): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val query = if (idCategoria > 0) {
            "SELECT * FROM ropa WHERE id_categoria = $idCategoria AND cantidad > 0"
        } else {
            "SELECT * FROM ropa WHERE cantidad > 0"
        }

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Ropa(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo")),
                        idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria")),
                        talla = cursor.getString(cursor.getColumnIndexOrThrow("talla")),
                        marca = cursor.getString(cursor.getColumnIndexOrThrow("marca")),
                        color = cursor.getString(cursor.getColumnIndexOrThrow("color")),
                        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun buscarRopa(texto: String): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ropa WHERE modelo LIKE '%$texto%' OR marca LIKE '%$texto%'", null
        )

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Ropa(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo")),
                        idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria")),
                        talla = cursor.getString(cursor.getColumnIndexOrThrow("talla")),
                        marca = cursor.getString(cursor.getColumnIndexOrThrow("marca")),
                        color = cursor.getString(cursor.getColumnIndexOrThrow("color")),
                        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun actualizar(ropa: Ropa): Boolean {
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
        val filasAfectadas = db.update("ropa", values, "id = ?", arrayOf(ropa.id.toString()))
        db.close()
        return filasAfectadas > 0
    }

    fun eliminar(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val filasAfectadas = db.delete("ropa", "id = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas > 0
    }

    fun obtenerPorId(id: Int): Ropa? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ropa WHERE id = ?", arrayOf(id.toString()))
        var ropa: Ropa? = null
        if (cursor.moveToFirst()) {
            ropa = Ropa(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo")),
                idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria")),
                talla = cursor.getString(cursor.getColumnIndexOrThrow("talla")),
                marca = cursor.getString(cursor.getColumnIndexOrThrow("marca")),
                color = cursor.getString(cursor.getColumnIndexOrThrow("color")),
                precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
            )
        }
        cursor.close()
        db.close()
        return ropa
    }

    // Obtener prendas con stock crítico (<= 3 unidades) (HU-12)
    fun listarStockCritico(): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ropa WHERE cantidad <= 3 ORDER BY cantidad ASC", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Ropa(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo")),
                        idCategoria = cursor.getInt(cursor.getColumnIndexOrThrow("id_categoria")),
                        talla = cursor.getString(cursor.getColumnIndexOrThrow("talla")),
                        marca = cursor.getString(cursor.getColumnIndexOrThrow("marca")),
                        color = cursor.getString(cursor.getColumnIndexOrThrow("color")),
                        precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                        cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}