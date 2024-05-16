package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.espacocultural.models.GlobalVariables
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArtInfoPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.art_info_page)

        // Botões superiores
        val returnButton = findViewById<Button>(R.id.return_button)
        val optionsButton = findViewById<ConstraintLayout>(R.id.options_button)

        returnButton.setOnClickListener {
            changeScreen(this, GlobalVariables.lastPage)
        }

        if (GlobalVariables.isAdmin) {
            optionsButton.visibility = View.VISIBLE
        } else {
            optionsButton.visibility = View.GONE
        }

        optionsButton.setOnClickListener {
            // Editar, remover
        }

        // Carregamento das obras
        val progressBar = findViewById<ProgressBar>(R.id.loader)
        progressBar.visibility = View.VISIBLE

        val mainContainer = findViewById<RelativeLayout>(R.id.mainContainer)
        mainContainer.visibility = View.GONE

        // Informações da Obra
        val artName = findViewById<TextView>(R.id.art_name)
        val artYear = findViewById<TextView>(R.id.art_year)
        val artAuthor = findViewById<TextView>(R.id.art_author)
        val artDescription = findViewById<TextView>(R.id.art_description)
        val artImage = findViewById<ImageView>(R.id.art_image)
        val expandedArtImage = findViewById<ImageView>(R.id.expanded_art_image) // Inicializa antes para expansão

        // Banco de dados
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("obras").document("Mona Lisa")

        docRef.get().addOnSuccessListener {
            if (it != null) {
                val name = it.data?.get("Nome da obra")?.toString()
                val year = it.data?.get("Ano")?.toString()
                val author = it.data?.get("Autor")?.toString()
                val description = it.data?.get("Descrição")?.toString()
                val image = it.data?.get("imagem").toString()

                val imageBitmap = decodeBase64ToBitmap(image)

                artName.text = name
                artYear.text = " - " + year
                artAuthor.text = author
                artDescription.text = description
                displayBitmapInImageView(imageBitmap, artImage) // Coloca a imagem em miniatura na obra
                displayBitmapInImageView(imageBitmap, expandedArtImage) // Coloca a imagem expandida

                progressBar.visibility = View.GONE
                mainContainer.visibility = View.VISIBLE
            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.VISIBLE
                mainContainer.visibility = View.GONE
            }

        // Tela expandida
        val expandedArt: FrameLayout = findViewById(R.id.expanded_art) // Tela da expansão
        val leaveExpansion: RelativeLayout = findViewById(R.id.leave_expansion) // Botão para sair de expansão
        val expandButton: ImageButton = findViewById(R.id.expand_button)

        expandButton.setOnClickListener {
            // Expande a imagem
            if (progressBar.visibility == View.GONE) {
                expandedArt.visibility = View.VISIBLE
            }
        }

        artImage.setOnClickListener {
            // Expande a imagem
            if (progressBar.visibility == View.GONE) {
                expandedArt.visibility = View.VISIBLE
            }
        }

        leaveExpansion.setOnClickListener {
            expandedArt.visibility = View.GONE
        }
    }

    fun changeScreen(activity: Activity, clasS: Class<*>?) {
        GlobalVariables.lastPage = activity::class.java
        val intent = Intent(activity, clasS)
        startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }

    // Função para decodificar a string Base64 em um objeto Bitmap
    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    // Função para exibir o Bitmap em uma ImageView
    private fun displayBitmapInImageView(bitmap: Bitmap?, imageView: ImageView) {
        bitmap?.let {
            imageView.setImageBitmap(it)
        }
    }
}