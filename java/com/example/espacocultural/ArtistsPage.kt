package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.espacocultural.models.GlobalVariables

class ArtistsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.artists_page)

        // Botões superiores
        val returnButton = findViewById<Button>(R.id.return_button)
        val optionsButton = findViewById<ConstraintLayout>(R.id.options_button)

        returnButton.setOnClickListener {
            changeScreen(this, HomePage::class.java)
        }

        if (GlobalVariables.isAdmin) {
            optionsButton.visibility = View.VISIBLE
        } else {
            optionsButton.visibility = View.GONE
        }

        optionsButton.setOnClickListener {
            // Editar, remover
        }

        // Botões artistas
        val artistButton1 = findViewById<Button>(R.id.artistButton1)

        artistButton1.setOnClickListener {
            changeScreen(this, ArtistInfoPage::class.java)
        }
    }

    fun changeScreen(activity: Activity, clasS: Class<*>?) {
        GlobalVariables.lastPage = activity::class.java
        val intent = Intent(activity, clasS)
        startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }
}