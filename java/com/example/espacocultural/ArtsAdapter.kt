package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.espacocultural.models.Arts
import com.example.espacocultural.models.GlobalVariables
import com.google.android.material.imageview.ShapeableImageView

class ArtsAdapter(private val artsList: List<Arts>) :
    RecyclerView.Adapter<ArtsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.arts_recycler_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArt = artsList[position]
        holder.nameTextView.text = currentArt.name

        // Configurando a imagem no ShapeableImageView
        holder.imageView.setImageResource(currentArt.image)

        // Definir um listener de clique para o botão
        holder.salonNumberButton.setOnClickListener {
            // Abrir a tela de ArtInfoPage, passando o identificador único da arte como parâmetro extra
            val intent = Intent(holder.itemView.context, ArtInfoPage::class.java)
            intent.putExtra("artId", currentArt.id)
            holder.itemView.context.startActivity(intent)
            GlobalVariables.lastPage = ArtsPage::class.java

            // Definir nenhuma animação de transição
            (holder.itemView.context as Activity).overridePendingTransition(0, 0)
        }
    }

    override fun getItemCount(): Int {
        return artsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageView: ShapeableImageView = itemView.findViewById(R.id.image)
        val salonNumberButton: RelativeLayout = itemView.findViewById(R.id.art_number_button)
    }
}