package com.senati.modaapp

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

        // Navegación del menú (se vinculará a sus pantallas en el siguiente paso)
        binding.cardRopa.setOnClickListener { }
        binding.cardPedidos.setOnClickListener { }
        binding.cardClientes.setOnClickListener { }
        binding.cardReportes.setOnClickListener { }

        // Botón Salir (HU-02, CA3)
        binding.btnSalir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}