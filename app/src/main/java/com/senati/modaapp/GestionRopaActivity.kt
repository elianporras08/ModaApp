package com.senati.modaapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.modaapp.databinding.ActivityGestionRopaBinding

class GestionRopaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionRopaBinding
    private lateinit var ropaDao: RopaDao
    private lateinit var adapter: GestionRopaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGestionRopaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)

        configurarRecyclerView()
        configurarBuscador()

        binding.btnNuevaPrenda.setOnClickListener {
            val intent = Intent(this, RegistrarRopaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPrendas(binding.etBuscarRopa.text.toString().trim())
    }

    private fun configurarRecyclerView() {
        binding.rvGestionRopa.layoutManager = LinearLayoutManager(this)

        adapter = GestionRopaAdapter(
            listaRopa = emptyList(),
            onEditarClick = { ropa ->
                // Abrir RegistrarRopaActivity en modo edición (HU-07)
                val intent = Intent(this, RegistrarRopaActivity::class.java).apply {
                    putExtra("EXTRA_ROPA_ID", ropa.id)
                }
                startActivity(intent)
            },
            onEliminarClick = { ropa ->
                confirmarEliminacion(ropa)
            }
        )
        binding.rvGestionRopa.adapter = adapter
    }

    private fun configurarBuscador() {
        binding.etBuscarRopa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cargarPrendas(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarPrendas(texto: String) {
        val lista = if (texto.isEmpty()) {
            ropaDao.listarDisponibles()
        } else {
            ropaDao.buscarRopa(texto)
        }
        adapter.actualizarLista(lista)
    }

    private fun confirmarEliminacion(ropa: Ropa) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar prenda")
            .setMessage("¿Estás seguro de eliminar '${ropa.modelo}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                val eliminado = ropaDao.eliminar(ropa.id)
                if (eliminado) {
                    Toast.makeText(this, "Prenda eliminada", Toast.LENGTH_SHORT).show()
                    cargarPrendas(binding.etBuscarRopa.text.toString().trim())
                } else {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}