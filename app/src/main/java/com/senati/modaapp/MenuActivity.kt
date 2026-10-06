package com.senati.modaapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.senati.modaapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usuario = intent.getStringExtra("EXTRA_USUARIO") ?: "admin"
        binding.tvBienvenida.text = "Hola, $usuario"

        binding.cardRopa.setOnClickListener {
            val intent = Intent(this, GestionRopaActivity::class.java)
            startActivity(intent)
        }

        binding.cardPedidos.setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }

        binding.cardClientes.setOnClickListener {
            val intent = Intent(this, ClientesActivity::class.java)
            startActivity(intent)
        }

        binding.cardReportes.setOnClickListener {
            val intent = Intent(this, ReportesActivity::class.java)
            startActivity(intent)
        }

        // Botón Salir: Limpiar sesión guardada (HU-13)
        binding.btnSalir.setOnClickListener {
            val prefs = getSharedPreferences("SesionModaApp", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}