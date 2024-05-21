package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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

class SettingsPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings_page)

        // Spinner
        val meuSpinner = findViewById<Spinner>(R.id.languagesSpinner)
        val opcoes = arrayOf("Português", "English", "Español")
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)

        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        meuSpinner.adapter = adaptador

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
}