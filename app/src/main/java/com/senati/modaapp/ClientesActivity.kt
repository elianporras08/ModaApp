package com.senati.modaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.modaapp.databinding.ActivityClientesBinding

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var adapter: ClientesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        configurarRecyclerView()
        cargarClientes()
    }

    override fun onResume() {
        super.onResume()
        cargarClientes()
    }

    private fun configurarRecyclerView() {
        binding.rvClientes.layoutManager = LinearLayoutManager(this)

        adapter = ClientesAdapter(emptyList())
        binding.rvClientes.adapter = adapter
    }

    private fun cargarClientes() {
        val lista = pedidoDao.obtenerClientesConPedidos()
        adapter.actualizarLista(lista)
    }
}