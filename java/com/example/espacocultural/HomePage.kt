package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.espacocultural.models.GlobalVariables

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_page)

        // Botões artistas
        val viewAllArtists: LinearLayout = findViewById(R.id.view_all_artists)
        val artistButton1 = findViewById<Button>(R.id.artistButton1)

        viewAllArtists.setOnClickListener{
            changeScreen(this, ArtistsPage::class.java)
        }

        artistButton1.setOnClickListener {
            changeScreen(this, ArtistInfoPage::class.java)
        }

        // Botões barra inferior
        val compassButton = findViewById<Button>(R.id.compassButton)
        val qrButton = findViewById<Button>(R.id.qrButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        compassButton.setOnClickListener{
            changeScreen(this, SalonsPage::class.java)
        }

        qrButton.setOnClickListener{
            changeScreen(this, QrPage::class.java)
        }

        settingsButton.setOnClickListener{
            changeScreen(this, SettingsPage::class.java)
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