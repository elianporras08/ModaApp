package com.senati.modaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.modaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón Ingresar como Administrador (HU-01, CA1, CA2, CA3)
        binding.btnIngresarAdmin.setOnClickListener {
            val usuarioInput = binding.etUsuario.text.toString().trim()
            val claveInput = binding.etClave.text.toString().trim()

            var isValid = true

            // Validar si el campo usuario está vacío (CA1)
            if (usuarioInput.isEmpty()) {
                binding.etUsuario.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etUsuario.error = null
            }

            // Validar si el campo contraseña está vacío (CA1)
            if (claveInput.isEmpty()) {
                binding.etClave.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etClave.error = null
            }

            if (isValid) {
                // Validación para el Sprint 1: admin / 1234 (CA2)
                if (usuarioInput == "admin" && claveInput == "1234") {
                    val intent = Intent(this, MenuActivity::class.java).apply {
                        putExtra("EXTRA_USUARIO", "admin")
                        putExtra("EXTRA_ROL", "ADMIN")
                    }
                    startActivity(intent)
                    finish() // Cierra el login para que la tecla 'atrás' no regrese aquí (CA2)
                } else {
                    // Credenciales incorrectas (CA3)
                    Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón Ver catálogo como cliente (HU-01, CA5)
        binding.btnVerCatalogo.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
        }
    }
}