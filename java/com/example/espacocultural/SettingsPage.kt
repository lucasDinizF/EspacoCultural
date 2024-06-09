package com.example.espacocultural

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.espacocultural.models.GlobalVariables
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SettingsPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Recupera a configuração de idioma de SharedPreferences
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("AppLanguage", "pt")
        GlobalVariables.appLanguage = savedLanguage ?: "pt"

        when (GlobalVariables.appLanguage) {
            "pt" -> changeLanguage(Locale("pt"))
            "en" -> changeLanguage(Locale("en"))
            "es" -> changeLanguage(Locale("es"))
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings_page)

        // Spinner
        val spinner = findViewById<Spinner>(R.id.languagesSpinner)
        val options = arrayOf("Português", "English", "Español")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Define a seleção inicial do Spinner de acordo com a linguagem atual
        val initialSelection = when (GlobalVariables.appLanguage) {
            "pt" -> 0
            "en" -> 1
            "es" -> 2
            else -> 0
        }
        spinner.setSelection(initialSelection)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                val newLanguage = when (selectedItem) {
                    "Português" -> "pt"
                    "English" -> "en"
                    "Español" -> "es"
                    else -> ""
                }

                if (GlobalVariables.appLanguage != newLanguage) {
                    GlobalVariables.appLanguage = newLanguage
                    saveLanguagePreference(newLanguage)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Suporte
        val supportVisibility = findViewById<LinearLayout>(R.id.support_visibility)
        val supportButton = findViewById<ImageButton>(R.id.support_button)
        var on: Boolean = true

        // Admin
        val admButton: Button = findViewById(R.id.enter_adm_button)
        val logout: RelativeLayout = findViewById(R.id.logout)

        if (GlobalVariables.isAdmin) {
            admButton.visibility = View.GONE
            logout.visibility = View.VISIBLE
        } else {
            admButton.visibility = View.VISIBLE
            logout.visibility = View.GONE
        }

        admButton.setOnClickListener {
            changeScreen(this, AdmLoginPage::class.java)
        }

        logout.setOnClickListener {
            Toast.makeText(this, "Você escolheu sair...", Toast.LENGTH_SHORT).show()
            GlobalVariables.isAdmin = false
            changeScreen(this, this::class.java)
        }

        // Barra Inferior
        val homeButton = findViewById<Button>(R.id.homeButton)
        val compassButton = findViewById<Button>(R.id.compassButton)
        val qrButton = findViewById<Button>(R.id.qrButton)

        supportButton.setOnClickListener{
            if (on) {
                val novaImagem = resources.getDrawable(R.drawable.arrow_down)
                supportVisibility.visibility = View.INVISIBLE
                supportButton.setImageDrawable(novaImagem)
                on = !on
            } else {
                val novaImagem = resources.getDrawable(R.drawable.arrow_up)
                supportVisibility.visibility = View.VISIBLE
                supportButton.setImageDrawable(novaImagem)
                on = !on
            }
        }

        homeButton.setOnClickListener {
            changeScreen(this, HomePage::class.java)
        }

        compassButton.setOnClickListener {
            changeScreen(this, SalonsPage::class.java)
        }

        qrButton.setOnClickListener {
            changeScreen(this, QrPage::class.java)
        }
    }

    fun changeScreen(activity: Activity, clasS: Class<*>?) {
        GlobalVariables.lastPage = activity::class.java
        val intent = Intent(activity, clasS)
        startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(0, 0); // Definindo nenhuma animação
    }

    private fun changeLanguage(locale: Locale) {
        val resources = this.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun saveLanguagePreference(language: String) {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("AppLanguage", language)
        editor.apply()
    }
}