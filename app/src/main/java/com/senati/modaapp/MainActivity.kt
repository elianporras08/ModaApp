package com.senati.modaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.senati.modaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDao = UsuarioDao(this)

        // Botón Ingresar como Administrador (HU-04, CA2, CA3)
        binding.btnIngresarAdmin.setOnClickListener {
            val usuarioInput = binding.etUsuario.text.toString().trim()
            val claveInput = binding.etClave.text.toString().trim()

            var isValid = true

            if (usuarioInput.isEmpty()) {
                binding.etUsuario.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etUsuario.error = null
            }

            if (claveInput.isEmpty()) {
                binding.etClave.error = getString(R.string.error_campo_vacio)
                isValid = false
            } else {
                binding.etClave.error = null
            }

            if (isValid) {
                // Validación real consultando la tabla usuario de modaapp.db (HU-04)
                val usuarioLogueado = usuarioDao.validarUsuario(usuarioInput, claveInput)

                if (usuarioLogueado != null) {
                    Toast.makeText(this, "Bienvenido ${usuarioLogueado.usuario}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MenuActivity::class.java).apply {
                        putExtra("EXTRA_USUARIO", usuarioLogueado.usuario)
                        putExtra("EXTRA_ROL", usuarioLogueado.rol)
                    }
                    startActivity(intent)
                    finish()
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