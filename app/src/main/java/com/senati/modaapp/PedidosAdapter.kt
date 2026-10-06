package com.senati.modaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.modaapp.databinding.ItemPedidoBinding

class PedidosAdapter(
    private var listaPedidos: List<PedidoItem>,
    private val onAtenderClick: (PedidoItem) -> Unit
) : RecyclerView.Adapter<PedidosAdapter.PedidosViewHolder>() {

    inner class PedidosViewHolder(val binding: ItemPedidoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidosViewHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidosViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        val binding = holder.binding

        binding.tvNumPedido.text = "Pedido #${pedido.id}"
        binding.tvFechaPedido.text = "Fecha: ${pedido.fecha}"
        binding.tvTotalPedido.text = String.format("Total: S/ %.2f", pedido.total)
        binding.tvEstadoPedido.text = pedido.estado

        if (pedido.estado == "ATENDIDO") {
            binding.btnAtenderPedido.visibility = View.GONE
            binding.tvEstadoPedido.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
            binding.tvEstadoPedido.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))
        } else {
            binding.btnAtenderPedido.visibility = View.VISIBLE
            binding.tvEstadoPedido.setTextColor(android.graphics.Color.parseColor("#C2185B"))
            binding.tvEstadoPedido.setBackgroundColor(android.graphics.Color.parseColor("#FCE4EC"))
        }

        binding.btnAtenderPedido.setOnClickListener {
            onAtenderClick(pedido)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size

    fun actualizarLista(nuevaLista: List<PedidoItem>) {
        listaPedidos = nuevaLista
        notifyDataSetChanged()
    }
}