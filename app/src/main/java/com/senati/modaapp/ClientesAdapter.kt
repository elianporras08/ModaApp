package com.senati.modaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.senati.modaapp.databinding.ItemClienteBinding

class ClientesAdapter(
    private var listaClientes: List<ClienteItem>
) : RecyclerView.Adapter<ClientesAdapter.ClientesViewHolder>() {

    inner class ClientesViewHolder(val binding: ItemClienteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientesViewHolder {
        val binding = ItemClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClientesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientesViewHolder, position: Int) {
        val cliente = listaClientes[position]
        val binding = holder.binding

        binding.tvNombreCliente.text = cliente.nombre
        binding.tvTelefonoCliente.text = "Teléfono: ${cliente.telefono}"
        binding.tvCantPedidosCliente.text = "${cliente.totalPedidos} pedidos"

        // Mostrar la inicial del cliente en el ícono circular
        binding.tvInicialCliente.text = if (cliente.nombre.isNotEmpty()) {
            cliente.nombre.substring(0, 1).uppercase()
        } else {
            "C"
        }
    }

    override fun getItemCount(): Int = listaClientes.size

    fun actualizarLista(nuevaLista: List<ClienteItem>) {
        listaClientes = nuevaLista
        notifyDataSetChanged()
    }
}