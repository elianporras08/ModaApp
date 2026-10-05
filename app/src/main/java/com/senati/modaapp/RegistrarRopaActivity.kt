package com.senati.modaapp

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.senati.modaapp.databinding.ActivityRegistrarRopaBinding

class RegistrarRopaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarRopaBinding
    private lateinit var ropaDao: RopaDao
    private var listaCategorias: List<Categoria> = emptyList()
    private var fotoUriSeleccionada: String = ""

    // Launcher para abrir la galería del dispositivo (HU-05)
    private val seleccionarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Intentar persistir permisos de lectura para la URI seleccionada
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            fotoUriSeleccionada = uri.toString()
            binding.ivFotoPrenda.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarRopaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)

        configurarSpinners()

        // Seleccionar foto de la galería
        binding.btnSeleccionarFoto.setOnClickListener {
            seleccionarFotoLauncher.launch("image/*")
        }

        // Guardar la prenda
        binding.btnGuardarRopa.setOnClickListener {
            guardarPrenda()
        }
    }

    private fun configurarSpinners() {
        // Cargar Categorías desde SQLite (HU-05)
        listaCategorias = ropaDao.listarCategorias()
        val nombresCategorias = listaCategorias.map { it.nombre }
        val adapterCategoria = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            nombresCategorias
        )
        binding.spCategoria.adapter = adapterCategoria

        // Cargar Tallas predefinidas
        val tallas = listOf("S", "M", "L", "XL", "28", "30", "32", "34", "36", "38", "40", "ESTÁNDAR")
        val adapterTalla = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tallas
        )
        binding.spTalla.adapter = adapterTalla
    }

    private fun guardarPrenda() {
        val modelo = binding.etModelo.text.toString().trim()
        val marca = binding.etMarca.text.toString().trim()
        val color = binding.etColor.text.toString().trim()
        val cantidadStr = binding.etCantidad.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()

        if (modelo.isEmpty() || marca.isEmpty() || color.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val cantidad = cantidadStr.toIntOrNull() ?: 0
        val precio = precioStr.toDoubleOrNull() ?: 0.0

        if (precio <= 0) {
            binding.etPrecio.error = "Ingresa un precio válido mayores a S/ 0"
            return
        }

        val posicionCat = binding.spCategoria.selectedItemPosition
        val idCategoria = if (listaCategorias.isNotEmpty()) listaCategorias[posicionCat].id else 1
        val tallaSeleccionada = binding.spTalla.selectedItem.toString()

        val nuevaRopa = Ropa(
            modelo = modelo,
            idCategoria = idCategoria,
            talla = tallaSeleccionada,
            marca = marca,
            color = color,
            precio = precio,
            cantidad = cantidad,
            foto = fotoUriSeleccionada
        )

        val insertado = ropaDao.insertar(nuevaRopa)

        if (insertado) {
            Toast.makeText(this, "Prenda registrada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar la prenda", Toast.LENGTH_SHORT).show()
        }
    }
}