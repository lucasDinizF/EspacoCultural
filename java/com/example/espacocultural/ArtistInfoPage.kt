package com.example.espacocultural

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.espacocultural.models.GlobalVariables
import java.util.Locale

class ArtistInfoPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.artist_info_page)

        if (GlobalVariables.appLanguage.equals("pt")) {
            changeLanguage(Locale("pt"))

        } else if (GlobalVariables.appLanguage.equals("en")) {
            changeLanguage(Locale("en"))

        } else {
            changeLanguage(Locale("es"))

        }

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