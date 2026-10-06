package com.senati.modaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.senati.modaapp.databinding.ActivityCatalogoBinding

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding
    private lateinit var ropaDao: RopaDao
    private lateinit var adapter: RopaAdapter
    private var listaCategorias: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)

        configurarRecyclerView()
        configurarFiltros()

        // Abrir la pantalla del carrito de compras (HU-02)
        binding.btnVerCarrito.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPrendas(0)
    }

    private fun configurarRecyclerView() {
        binding.recyclerCatalogo.layoutManager = GridLayoutManager(this, 2)

        adapter = RopaAdapter(emptyList()) { prenda ->
            // Agregar el producto seleccionado al CarritoManager (HU-08)
            CarritoManager.agregarProducto(prenda)
            Toast.makeText(this, "${prenda.modelo} añadido al carrito", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerCatalogo.adapter = adapter
    }

    private fun configurarFiltros() {
        listaCategorias = ropaDao.listarCategorias()

        binding.chipGroupCategorias.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val checkedId = checkedIds[0]
            val idCategoria = when (checkedId) {
                R.id.chipPolos -> obtenerIdCategoria("Polos")
                R.id.chipPantalones -> obtenerIdCategoria("Pantalones")
                R.id.chipVestidos -> obtenerIdCategoria("Vestidos")
                else -> 0
            }

            cargarPrendas(idCategoria)
        }
    }

    private fun obtenerIdCategoria(nombre: String): Int {
        return listaCategorias.find { it.nombre.equals(nombre, ignoreCase = true) }?.id ?: 0
    }

    private fun cargarPrendas(idCategoria: Int) {
        val lista = ropaDao.listarDisponibles(idCategoria)
        adapter.actualizarLista(lista)
    }
}