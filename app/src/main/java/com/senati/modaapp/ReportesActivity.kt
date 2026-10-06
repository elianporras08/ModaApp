package com.senati.modaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.modaapp.databinding.ActivityReportesBinding

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var ropaDao: RopaDao
    private lateinit var adapter: GestionRopaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)
        ropaDao = RopaDao(this)

        configurarRecyclerView()
        cargarMetricas()
    }

    override fun onResume() {
        super.onResume()
        cargarMetricas()
    }

    private fun configurarRecyclerView() {
        binding.rvStockCritico.layoutManager = LinearLayoutManager(this)

        adapter = GestionRopaAdapter(
            listaRopa = emptyList(),
            onEditarClick = {},
            onEliminarClick = {}
        )
        binding.rvStockCritico.adapter = adapter
    }

    private fun cargarMetricas() {
        // 1. Cargar monto acumulado y cantidad de pedidos atendidos (HU-12)
        val totalVentas = pedidoDao.obtenerTotalVentasAtendidas()
        val cantidadPedidos = pedidoDao.obtenerCantidadPedidosAtendidos()

        binding.tvMontoTotalVentas.text = String.format("S/ %.2f", totalVentas)
        binding.tvCantidadPedidosAtendidos.text = "$cantidadPedidos pedidos"

        // 2. Cargar lista de prendas con stock crítico (HU-12)
        val listaCritica = ropaDao.listarStockCritico()
        adapter.actualizarLista(listaCritica)
    }
}