package com.senati.modaapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.modaapp.databinding.ActivityPedidosBinding

class PedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidosBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var adapter: PedidosAdapter
    private var estadoActual = "PENDIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        configurarRecyclerView()
        configurarFiltros()
    }

    override fun onResume() {
        super.onResume()
        cargarPedidos(estadoActual)
    }

    private fun configurarRecyclerView() {
        binding.rvPedidos.layoutManager = LinearLayoutManager(this)

        adapter = PedidosAdapter(
            listaPedidos = emptyList(),
            onAtenderClick = { pedido ->
                confirmarAtencion(pedido)
            }
        )
        binding.rvPedidos.adapter = adapter
    }

    private fun configurarFiltros() {
        binding.btnFiltroPendientes.setOnClickListener {
            estadoActual = "PENDIENTE"
            actualizarEstiloBotones(esPendiente = true)
            cargarPedidos(estadoActual)
        }

        binding.btnFiltroAtendidos.setOnClickListener {
            estadoActual = "ATENDIDO"
            actualizarEstiloBotones(esPendiente = false)
            cargarPedidos(estadoActual)
        }
    }

    private fun actualizarEstiloBotones(esPendiente: Boolean) {
        if (esPendiente) {
            binding.btnFiltroPendientes.setBackgroundColor(getColor(R.color.rosa_moda))
            binding.btnFiltroPendientes.setTextColor(getColor(R.color.white))

            binding.btnFiltroAtendidos.setBackgroundColor(getColor(android.R.color.transparent))
            binding.btnFiltroAtendidos.setTextColor(getColor(R.color.rosa_moda))
        } else {
            binding.btnFiltroAtendidos.setBackgroundColor(getColor(R.color.rosa_moda))
            binding.btnFiltroAtendidos.setTextColor(getColor(R.color.white))

            binding.btnFiltroPendientes.setBackgroundColor(getColor(android.R.color.transparent))
            binding.btnFiltroPendientes.setTextColor(getColor(R.color.rosa_moda))
        }
    }

    private fun cargarPedidos(estado: String) {
        val lista = pedidoDao.listarPorEstado(estado)
        adapter.actualizarLista(lista)
    }

    private fun confirmarAtencion(pedido: PedidoItem) {
        AlertDialog.Builder(this)
            .setTitle("Atender pedido")
            .setMessage("¿Deseas marcar el Pedido #${pedido.id} como ATENDIDO y descontar el stock de prendas?")
            .setPositiveButton("Sí, atender") { _, _ ->
                val exito = pedidoDao.atenderPedido(pedido.id)
                if (exito) {
                    Toast.makeText(this, "Pedido #${pedido.id} atendido correctamente", Toast.LENGTH_SHORT).show()
                    cargarPedidos(estadoActual)
                } else {
                    Toast.makeText(this, "Error o stock insuficiente para atender el pedido", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}