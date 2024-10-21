package com.example.abcallapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abcallapp.R
import com.example.abcallapp.data.model.PQRItem

class PQRAdapter(
    private val pqrList: List<PQRItem>,
    private val onItemClick: (PQRItem) -> Unit  // Lambda para manejar el clic
) : RecyclerView.Adapter<PQRAdapter.PQRViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PQRViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pqr, parent, false)
        return PQRViewHolder(view)
    }

    override fun onBindViewHolder(holder: PQRViewHolder, position: Int) {
        val pqrItem = pqrList[position]
        holder.bind(pqrItem)

        // Asignar el click listener
        holder.itemView.setOnClickListener {
            onItemClick(pqrItem)  // Llamada al lambda cuando se hace clic
        }
    }

    override fun getItemCount() = pqrList.size

    class PQRViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pqrTextView: TextView = itemView.findViewById(R.id.pqrTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.pqrStatusTextView)

        fun bind(pqrItem: PQRItem) {
            pqrTextView.text = "${pqrItem.date} - ${pqrItem.subject}"

            // Cambia el color dependiendo del estado
            when (pqrItem.status.lowercase()) { // Convertimos el texto a minúsculas para hacer la comparación insensible a mayúsculas/minúsculas
                "open", "abierto" -> {  // Verificamos si el estado es "open" o "abierto"
                    statusTextView.text = pqrItem.status
                    statusTextView.setTextColor(
                        ContextCompat.getColor(itemView.context, androidx.appcompat.R.color.material_deep_teal_500)
                    )
                }
                else -> {
                    statusTextView.text = pqrItem.status
                    statusTextView.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.red)
                    )
                }
            }

        }
    }
}


