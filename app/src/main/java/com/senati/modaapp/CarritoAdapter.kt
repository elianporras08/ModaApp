package com.senati.modaapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.modaapp.databinding.ItemCarritoBinding

class CarritoAdapter(
    private var listaItems: List<ItemCarrito>,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(val binding: ItemCarritoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = listaItems[position]
        val binding = holder.binding

        binding.tvModeloCarrito.text = "${item.ropa.modelo} · ${item.ropa.talla}"
        binding.tvDetalleCarrito.text = "${item.ropa.color} · ${item.cantidad} x S/ ${String.format("%.2f", item.ropa.precio)}"
        binding.tvSubtotalCarrito.text = String.format("S/ %.2f", item.subtotal)

        if (item.ropa.foto.isNotEmpty()) {
            try {
                binding.ivFotoCarrito.setImageURI(Uri.parse(item.ropa.foto))
            } catch (e: Exception) {
                binding.ivFotoCarrito.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            binding.ivFotoCarrito.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Eliminar ítem al mantener presionado (Prototipo P2-05)
        binding.root.setOnLongClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onItemLongClick(pos)
            }
            true
        }
    }

    override fun getItemCount(): Int = listaItems.size

    fun actualizarLista(nuevaLista: List<ItemCarrito>) {
        listaItems = nuevaLista
        notifyDataSetChanged()
    }
}