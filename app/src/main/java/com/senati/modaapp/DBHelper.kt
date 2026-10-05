package com.senati.modaapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "modaapp.db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de Usuarios (HU-04)
        db.execSQL("""
            CREATE TABLE usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE,
                clave TEXT,
                rol TEXT,
                telefono TEXT
            )
        """.trimIndent())

        // Tabla de Categorías (HU-05, HU-06)
        db.execSQL("""
            CREATE TABLE categoria (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE
            )
        """.trimIndent())

        // Tabla de Ropa (HU-05)
        db.execSQL("""
            CREATE TABLE ropa (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                modelo TEXT NOT NULL,
                id_categoria INTEGER REFERENCES categoria(id),
                talla TEXT,
                marca TEXT,
                color TEXT,
                precio REAL CHECK(precio > 0),
                cantidad INTEGER CHECK(cantidad >= 0),
                foto TEXT
            )
        """.trimIndent())

        // Insertar usuario administrador por defecto (HU-04)
        db.execSQL("INSERT INTO usuario (usuario, clave, rol, telefono) VALUES ('admin', '1234', 'ADMIN', '987654321')")

        // Precargar las 5 categorías oficiales (HU-05, HU-06)
        db.execSQL("INSERT INTO categoria (nombre) VALUES ('Polos')")
        db.execSQL("INSERT INTO categoria (nombre) VALUES ('Pantalones')")
        db.execSQL("INSERT INTO categoria (nombre) VALUES ('Vestidos')")
        db.execSQL("INSERT INTO categoria (nombre) VALUES ('Casacas')")
        db.execSQL("INSERT INTO categoria (nombre) VALUES ('Zapatillas')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Las migraciones de tablas para pedidos se gestionarán en el Sprint 3 (DB_VERSION = 2)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}