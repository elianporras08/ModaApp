package com.senati.modaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.senati.modaapp.databinding.ActivityCarritoBinding
import java.net.URLEncoder

class CarritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var adapter: CarritoAdapter
    private lateinit var pedidoDao: PedidoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        configurarRecyclerView()
        actualizarResumen()

        // Botón Hacer pedido (HU-09 / HU-10)
        binding.btnHacerPedido.setOnClickListener {
            procesarPedido()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.actualizarLista(CarritoManager.obtenerItems())
        actualizarResumen()
    }

    private fun configurarRecyclerView() {
        binding.recyclerCarrito.layoutManager = LinearLayoutManager(this)

        adapter = CarritoAdapter(CarritoManager.obtenerItems()) { posicion ->
            CarritoManager.eliminarItem(posicion)
            adapter.actualizarLista(CarritoManager.obtenerItems())
            actualizarResumen()
            Toast.makeText(this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerCarrito.adapter = adapter
    }

    private fun actualizarResumen() {
        val total = CarritoManager.obtenerTotal()
        binding.tvTotalCarrito.text = String.format("S/ %.2f", total)
    }

    private fun procesarPedido() {
        val items = CarritoManager.obtenerItems()

        if (items.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val total = CarritoManager.obtenerTotal()

        // 1. Guardar en SQLite
        val guardado = pedidoDao.registrarPedido(items, total)

        if (guardado) {
            // 2. Construir mensaje de WhatsApp
            val mensajeBuilder = StringBuilder()
            mensajeBuilder.append("¡Hola ModaApp! 👋 Deseo realizar el siguiente pedido:\n\n")

            for (item in items) {
                mensajeBuilder.append("• ${item.ropa.modelo} (${item.ropa.talla} / ${item.ropa.color}) x${item.cantidad} - S/ ${String.format("%.2f", item.subtotal)}\n")
            }

            mensajeBuilder.append("\n*Total: S/ ${String.format("%.2f", total)}*")

            // 3. Limpiar carrito en memoria
            CarritoManager.limpiarCarrito()
            adapter.actualizarLista(emptyList())
            actualizarResumen()

            // 4. Abrir WhatsApp
            enviarAWhatsApp(mensajeBuilder.toString())
        } else {
            Toast.makeText(this, "Error al guardar el pedido en la base de datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarAWhatsApp(mensaje: String) {
        try {
            val numeroTelefono = "51999999999" // Número de prueba de la tienda
            val mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$numeroTelefono&text=$mensajeCodificado")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Pedido registrado. No se pudo abrir WhatsApp.", Toast.LENGTH_LONG).show()
        }
    }
}