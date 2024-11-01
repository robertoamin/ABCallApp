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
        private val stateBubble: TextView = itemView.findViewById(R.id.stateBubble)  // Burbuja de estado

        fun bind(pqrItem: PQRItem) {
            // Truncar el subject si es mayor a 20 caracteres
            val truncatedSubject = if (pqrItem.subject.length > 20) {
                pqrItem.subject.substring(0, 20) + "..."
            } else {
                pqrItem.subject
            }

            // Mostrar la fecha y el subject truncado
            pqrTextView.text = "${pqrItem.date} - $truncatedSubject"

            // Cambia el color dependiendo del estado
            when (pqrItem.status.lowercase()) { // Convertimos el texto a minúsculas para hacer la comparación insensible a mayúsculas/minúsculas
                "open", "abierto" -> {  // Verificamos si el estado es "open" o "abierto"
                    stateBubble.text = "A"
                    stateBubble.setBackgroundResource(R.drawable.green_bubble_background)
                    stateBubble.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))  // Texto negro para "A"
                }
                else -> {
                    stateBubble.text = "C"
                    stateBubble.setBackgroundResource(R.drawable.dark_gray_bubble_background)
                    stateBubble.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                }
            }

        }
    }
}


