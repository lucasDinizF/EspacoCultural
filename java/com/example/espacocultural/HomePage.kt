package com.example.espacocultural

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.espacocultural.models.GlobalVariables
import java.util.Locale

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_page)

        if (GlobalVariables.appLanguage.equals("pt")) {
            changeLanguage(Locale("pt"))

        } else if (GlobalVariables.appLanguage.equals("en")) {
            changeLanguage(Locale("en"))

        } else {
            changeLanguage(Locale("es"))

        }

        // Botões artistas
        val viewAllArtists: LinearLayout = findViewById(R.id.view_all_artists)

        viewAllArtists.setOnClickListener{
            changeScreen(this, ArtistsPage::class.java)
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

    fun updateLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val config = resources.configuration
        val displayMetrics = resources.displayMetrics

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            config.locale = locale
        }

        resources.updateConfiguration(config, displayMetrics)
    }

    private fun changeLanguage(locale: Locale) {
        updateLocale(this, locale)
        // Recria a atividade para aplicar a nova configuração de idioma
        val intent = Intent(this, this::class.java)
        startActivity(intent)
        finish()
    }
}