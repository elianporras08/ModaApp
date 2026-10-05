package com.senati.modaapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.modaapp.databinding.ItemRopaBinding

class RopaAdapter(
    private var listaRopa: List<Ropa>,
    private val onAgregarClick: (Ropa) -> Unit
) : RecyclerView.Adapter<RopaAdapter.RopaViewHolder>() {

    inner class RopaViewHolder(val binding: ItemRopaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RopaViewHolder {
        val binding = ItemRopaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RopaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RopaViewHolder, position: Int) {
        val ropa = listaRopa[position]
        with(holder.binding) {
            tvModeloItem.text = ropa.modelo
            tvDetalleItem.text = "Talla ${ropa.talla} · ${ropa.color}"
            tvPrecioItem.text = String.format("S/ %.2f", ropa.precio)

            if (ropa.foto.isNotEmpty()) {
                try {
                    ivFotoItem.setImageURI(Uri.parse(ropa.foto))
                } catch (e: Exception) {
                    ivFotoItem.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                ivFotoItem.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            btnAgregarItem.setOnClickListener {
                onAgregarClick(ropa)
            }
        }
    }

    override fun getItemCount(): Int = listaRopa.size

    fun actualizarLista(nuevaLista: List<Ropa>) {
        listaRopa = nuevaLista
        notifyDataSetChanged()
    }
}