package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.espacocultural.models.Arts
import com.example.espacocultural.models.GlobalVariables
import com.example.espacocultural.models.Salons

class ArtsPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArtsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.arts_page)

        // Inicializa o RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Cria e define o adaptador para o RecyclerView
        val artsList = getListOfArts() // Método para obter a lista de artes
        adapter = ArtsAdapter(artsList)
        recyclerView.adapter = adapter

        // Botões barra superior & Adicionar
        val returnButton = findViewById<Button>(R.id.return_button) // Botão de voltar à tela anterior
        val optionsButton: ConstraintLayout = findViewById(R.id.options_button) // Botão de Editar e Remover
        val addArt: RelativeLayout = findViewById(R.id.add) // Botão para adicionar obra

        // Card de criar salão
        val outsideCard: FrameLayout = findViewById(R.id.art_creation_background) // Layout do card
        val leaveButton: RelativeLayout = findViewById(R.id.leave_card) // Botão de sair do card
        val errorPrevention: FrameLayout = findViewById(R.id.error_prevention_background) // Card de prevenção de erros

        if (GlobalVariables.isAdmin) {
            optionsButton.visibility = View.VISIBLE
            addArt.visibility = View.VISIBLE
        } else {
            optionsButton.visibility = View.GONE
            addArt.visibility = View.GONE
        }

        returnButton.setOnClickListener {
            changeScreen(this, SalonsPage::class.java)
        }

        addArt.setOnClickListener {
            outsideCard.visibility = View.VISIBLE
        }

        optionsButton.setOnClickListener {
            // Editar, remover
        }

        leaveButton.setOnClickListener {
            // Prevenção de erros
            errorPrevention.visibility = View.VISIBLE
        }

        val cancelButton: Button = findViewById(R.id.cancel_button) // Botão para cancelar a operação
        val confirmButton: Button = findViewById(R.id.confirm_button) // Botão para confirmar a operação

        cancelButton.setOnClickListener {
            // Cancela a saída e volta à tela de adicionar obra
            errorPrevention.visibility = View.GONE
        }

        confirmButton.setOnClickListener {
            // Confirma a saída e volta à tela das obras
            errorPrevention.visibility = View.GONE
            outsideCard.visibility = View.GONE
        }

        val createArt: Button = findViewById(R.id.create_art) // Botão para adicionar a obra dentro do RecyclerView

        createArt.setOnClickListener {
            // Adiciona a obra no RecyclerView
        }
    }

    fun changeScreen(activity: Activity, clasS: Class<*>?) {
        GlobalVariables.lastPage = activity::class.java
        val intent = Intent(activity, clasS)
        startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }

    private fun getListOfArts(): List<Arts> {
        // Aqui você deve retornar a lista de objetos Arts com os dados do seu banco de dados ou de onde quer que venham os dados
        // Por enquanto, vamos retornar uma lista de exemplos
        val list = mutableListOf<Arts>()
        list.add(Arts(1,"Arte 1", R.drawable.arte_exemplo))
        list.add(Arts(2,"Arte 2", R.drawable.arte_exemplo))
        list.add(Arts(3,"Arte 3", R.drawable.arte_exemplo))
        list.add(Arts(4,"Arte 4", R.drawable.arte_exemplo))
        list.add(Arts(5,"Arte 5", R.drawable.arte_exemplo))
        list.add(Arts(6,"Arte 6", R.drawable.arte_exemplo))
        list.add(Arts(7,"Arte 7", R.drawable.arte_exemplo))
        list.add(Arts(8,"Arte 8", R.drawable.arte_exemplo))
        // Adicione mais salões conforme necessário
        return list
    }
}