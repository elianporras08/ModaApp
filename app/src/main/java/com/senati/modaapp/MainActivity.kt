package com.senati.modaapp

import android.content.Context
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

        // Verificar si existe una sesión activa recordada (HU-13)
        val prefs = getSharedPreferences("SesionModaApp", Context.MODE_PRIVATE)
        val usuarioGuardado = prefs.getString("KEY_USUARIO", null)

        if (usuarioGuardado != null) {
            abrirMenu(usuarioGuardado)
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDao = UsuarioDao(this)

        // Botón Ingresar como Administrador (HU-04)
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
                val usuarioLogueado = usuarioDao.validarUsuario(usuarioInput, claveInput)

                if (usuarioLogueado != null) {
                    // Guardar sesión activa (HU-13)
                    prefs.edit().putString("KEY_USUARIO", usuarioLogueado.usuario).apply()

                    Toast.makeText(this, "Bienvenido ${usuarioLogueado.usuario}", Toast.LENGTH_SHORT).show()
                    abrirMenu(usuarioLogueado.usuario)
                } else {
                    Toast.makeText(this, getString(R.string.error_credenciales), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón Ver catálogo como cliente (HU-01)
        binding.btnVerCatalogo.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun abrirMenu(usuario: String) {
        val intent = Intent(this, MenuActivity::class.java).apply {
            putExtra("EXTRA_USUARIO", usuario)
        }
        startActivity(intent)
        finish()
    }
}