package com.senati.modaapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.modaapp.databinding.ItemGestionRopaBinding

class GestionRopaAdapter(
    private var listaRopa: List<Ropa>,
    private val onEditarClick: (Ropa) -> Unit,
    private val onEliminarClick: (Ropa) -> Unit
) : RecyclerView.Adapter<GestionRopaAdapter.GestionViewHolder>() {

    inner class GestionViewHolder(val binding: ItemGestionRopaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GestionViewHolder {
        val binding = ItemGestionRopaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GestionViewHolder, position: Int) {
        val ropa = listaRopa[position]
        val binding = holder.binding

        binding.tvModeloGestion.text = ropa.modelo
        binding.tvDetalleGestion.text = "Marca: ${ropa.marca} · Stock: ${ropa.cantidad}"
        binding.tvPrecioGestion.text = String.format("S/ %.2f", ropa.precio)

        if (ropa.foto.isNotEmpty()) {
            try {
                binding.ivFotoGestion.setImageURI(Uri.parse(ropa.foto))
            } catch (e: Exception) {
                binding.ivFotoGestion.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            binding.ivFotoGestion.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        binding.btnEditarRopa.setOnClickListener { onEditarClick(ropa) }
        binding.btnEliminarRopa.setOnClickListener { onEliminarClick(ropa) }
    }

    override fun getItemCount(): Int = listaRopa.size

    fun actualizarLista(nuevaLista: List<Ropa>) {
        listaRopa = nuevaLista
        notifyDataSetChanged()
    }
}