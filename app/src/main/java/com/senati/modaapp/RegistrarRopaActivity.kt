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
    private var idRopaEditar: Int = -1

    private val seleccionarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
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

        // Capturar si viene un ID para modo edición (HU-07)
        idRopaEditar = intent.getIntExtra("EXTRA_ROPA_ID", -1)

        configurarSpinners()

        if (idRopaEditar != -1) {
            cargarDatosEdicion()
        }

        binding.btnSeleccionarFoto.setOnClickListener {
            seleccionarFotoLauncher.launch("image/*")
        }

        binding.btnGuardarRopa.setOnClickListener {
            guardarOActualizarPrenda()
        }
    }

    private fun configurarSpinners() {
        listaCategorias = ropaDao.listarCategorias()
        val nombresCategorias = listaCategorias.map { it.nombre }
        val adapterCategoria = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            nombresCategorias
        )
        binding.spCategoria.adapter = adapterCategoria

        val tallas = listOf("S", "M", "L", "XL", "28", "30", "32", "34", "36", "38", "40", "ESTÁNDAR")
        val adapterTalla = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tallas
        )
        binding.spTalla.adapter = adapterTalla
    }

    private fun cargarDatosEdicion() {
        val ropa = ropaDao.obtenerPorId(idRopaEditar) ?: return

        binding.tvTituloRegistrar.text = "Editar prenda"
        binding.btnGuardarRopa.text = "Actualizar"

        binding.etModelo.setText(ropa.modelo)
        binding.etMarca.setText(ropa.marca)
        binding.etColor.setText(ropa.color)
        binding.etCantidad.setText(ropa.cantidad.toString())
        binding.etPrecio.setText(ropa.precio.toString())

        fotoUriSeleccionada = ropa.foto
        if (ropa.foto.isNotEmpty()) {
            try {
                binding.ivFotoPrenda.setImageURI(Uri.parse(ropa.foto))
            } catch (e: Exception) {
                binding.ivFotoPrenda.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }

        // Posicionar spinner categoría
        val posCat = listaCategorias.indexOfFirst { it.id == ropa.idCategoria }
        if (posCat != -1) binding.spCategoria.setSelection(posCat)

        // Posicionar spinner talla
        val tallas = listOf("S", "M", "L", "XL", "28", "30", "32", "34", "36", "38", "40", "ESTÁNDAR")
        val posTalla = tallas.indexOf(ropa.talla)
        if (posTalla != -1) binding.spTalla.setSelection(posTalla)
    }

    private fun guardarOActualizarPrenda() {
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
            binding.etPrecio.error = "Ingresa un precio válido mayor a S/ 0"
            return
        }

        val posicionCat = binding.spCategoria.selectedItemPosition
        val idCategoria = if (listaCategorias.isNotEmpty()) listaCategorias[posicionCat].id else 1
        val tallaSeleccionada = binding.spTalla.selectedItem.toString()

        val ropaObjeto = Ropa(
            id = if (idRopaEditar != -1) idRopaEditar else 0,
            modelo = modelo,
            idCategoria = idCategoria,
            talla = tallaSeleccionada,
            marca = marca,
            color = color,
            precio = precio,
            cantidad = cantidad,
            foto = fotoUriSeleccionada
        )

        val exito = if (idRopaEditar != -1) {
            ropaDao.actualizar(ropaObjeto)
        } else {
            ropaDao.insertar(ropaObjeto)
        }

        if (exito) {
            val msj = if (idRopaEditar != -1) "Prenda actualizada correctamente" else "Prenda registrada correctamente"
            Toast.makeText(this, msj, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al procesar la prenda", Toast.LENGTH_SHORT).show()
        }
    }
}