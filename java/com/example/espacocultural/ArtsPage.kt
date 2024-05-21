package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.espacocultural.models.Arts
import com.example.espacocultural.models.GlobalVariables
import com.example.espacocultural.models.Salons
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.io.ByteArrayOutputStream

class ArtsPage : AppCompatActivity(), ArtsAdapter.OnItemClickListener {

    private var salonId: Int? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArtsAdapter
    private lateinit var addImage: ImageView
    private var artsListener: ListenerRegistration? = null
    private var artsList = mutableListOf<Arts>()

    private val db = FirebaseFirestore.getInstance()

    private var selectedImage: Boolean = false
    private var inOptions: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.arts_page)

        // Qual salão estou?
        salonId = intent.getIntExtra("salonId", -1)

        // Inicializa o RecyclerView
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Cria e define o adaptador para o RecyclerView
        adapter = ArtsAdapter(artsList, this, salonId!!)
        recyclerView.adapter = adapter
        loadArtsFromFirestore()

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

            addImage = findViewById(R.id.add_image)
            val artName: EditText = findViewById(R.id.art_name)
            val artAuthor: EditText = findViewById(R.id.art_author)
            val artDescription: EditText = findViewById(R.id.art_description)

            val artYear: EditText = findViewById(R.id.art_year)
            configEditTextToOnlyInteger(artYear)

            val createArt: Button = findViewById(R.id.create_art)

            // Abre a galeria do celular e seleciona imagem
            addImage.setOnClickListener {
                openGallery()
            }

            createArt.setOnClickListener {
                // Adiciona número e imagem, fazer if (se não tiver imagem ou texto, não adicionar)
                if (addImage.drawable != null && (artName.text.toString() != "" || artYear.text.toString() != ""
                            || artAuthor.text.toString() != "" || artDescription.text.toString() != "")) {
                    val art = mapOf(
                        "Ano" to artYear.text.toString(),
                        "Autor" to artAuthor.text.toString(),
                        "Descrição" to artDescription.text.toString(),
                        "Nome da obra" to artName.text.toString(),
                        "imagem" to imageViewToBase64(addImage)
                    )

                    db.collection("saloes").document("salao $salonId")
                        .collection("obras").document(artName.text.toString())
                        .set(art)

                    outsideCard.visibility = View.GONE
                    artName.text.clear()
                    artYear.text.clear()
                    artAuthor.text.clear()
                    artDescription.text.clear()
                    addImage.setImageDrawable(null)

                    loadArtsFromFirestore()
                }  else {
                    Toast.makeText(this, "A obra está sem imagem ou algum campo não foi preenchido!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        optionsButton.setOnClickListener {
            // Editar, remover (pegar os botões do RecyclerView)
            if (!inOptions) {
                artsList.forEach { it.showOptions = true }
                adapter.notifyDataSetChanged()

                optionsChangeSizeAndImage(20, R.drawable.x_white)
            } else {
                artsList.forEach { it.showOptions = false }
                adapter.notifyDataSetChanged()

                optionsChangeSizeAndImage(30, R.drawable.options)
            }

            inOptions = !inOptions
        }

        leaveButton.setOnClickListener {
            // Prevenção de erros
            val errorPreventionText: TextView = findViewById(R.id.error_prevention_text)
            errorPreventionText.text = "Você tem certeza que deseja sair da criação de obra?"
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

            val artName: EditText = findViewById(R.id.art_name)
            val artYear: EditText = findViewById(R.id.art_year)
            val artAuthor: EditText = findViewById(R.id.art_author)
            val artDescription: EditText = findViewById(R.id.art_description)

            artName.text.clear()
            artYear.text.clear()
            artAuthor.text.clear()
            artDescription.text.clear()
            addImage.setImageDrawable(null)
        }
    }

    override fun onStart() {
        super.onStart()
        // Recarregar dados do Firestore quando a activity é iniciada
        loadArtsFromFirestore()
    }

    override fun onStop() {
        super.onStop()
        // Remover o listener para evitar memory leaks
        artsListener?.remove()
    }

    private fun loadArtsFromFirestore() {
        artsListener = db.collection("saloes").document("salao $salonId")
            .collection("obras")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val tempList = mutableListOf<Arts>()

                    for (document in snapshot.documents) {
                        val artData = document.data
                        if (artData != null && artData["Nome da obra"] != "ignore") {
                            val name = artData["Nome da obra"] as String
                            val year = artData["Ano"] as String
                            val author = artData["Autor"] as String
                            val description = artData["Descrição"] as String
                            val base64Image = artData["imagem"] as String

                            val image = decodeBase64ToDrawable(base64Image)

                            // Crie um objeto Art com os dados recuperados
                            val art = Arts(name, year.toInt(), author, description, image)
                            tempList.add(art)
                        }
                    }

                    // Atualize a lista e notifique o Adapter
                    artsList.clear()
                    artsList.addAll(tempList)
                    adapter.notifyDataSetChanged()
                }
            }
    }

    fun changeScreen(activity: Activity, clasS: Class<*>?) {
        GlobalVariables.lastPage = activity::class.java
        val intent = Intent(activity, clasS)
        startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }

    private fun imageViewToBase64(imageView: ImageView): String {
        // Obtém o drawable da imageView
        val drawable = imageView.drawable as BitmapDrawable
        // Obtém o bitmap do drawable
        val bitmap = drawable.bitmap
        // Converte o bitmap em uma string Base64
        return bitmapToBase64(bitmap)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun decodeBase64ToDrawable(base64Image: String): Drawable? {
        val decodedBytes: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        return bitmap?.let { BitmapDrawable(resources, it) }
    }

    private fun optionsChangeSizeAndImage(size: Int, image: Int) {
        val optionsImage: ImageView = findViewById(R.id.options_image)
        optionsImage.setImageResource(image)

        val params = optionsImage.layoutParams
        val density = resources.displayMetrics.density

        params.width = (size * density).toInt()
        params.height = (size * density).toInt()

        optionsImage.layoutParams = params
    }

    fun configEditTextToOnlyInteger(editText: EditText) {
        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

        val maxLength = 4 // Defina o número máximo de dígitos permitidos (opcional)

        // Combina os filtros de entrada e comprimento
        editText.filters = arrayOf(inputFilter, InputFilter.LengthFilter(maxLength))
    }

    private fun openGallery() {
        // Cria um intent para abrir a galeria
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        // Inicia a atividade da galeria com o resultado esperado
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Imagem selecionada com sucesso
            val data: Intent? = result.data
            val selectedImageUri = data?.data

            // Carrega a imagem selecionada no ImageView usando Glide
            selectedImageUri?.let {
                Glide.with(this)
                    .load(it)
                    .into(addImage)

                selectedImage = true
            }
        }
    }

    override fun onDeleteIconClick(position: Int) {
        val selectedArt = artsList[position]

        val deleteCard: FrameLayout = findViewById(R.id.delete_error_prevention)
        deleteCard.visibility = View.VISIBLE

        val cancelDeletion: Button = findViewById(R.id.cancel_delete_button)
        val confirmDeletion: Button = findViewById(R.id.confirm_delete_button)

        cancelDeletion.setOnClickListener {
            // Cancela a deleção
            deleteCard.visibility = View.GONE
        }

        confirmDeletion.setOnClickListener {
            // Confirma a deleção
            deleteCard.visibility = View.GONE
            var oneElement: Boolean = false

            if (artsList.size == 1) {
                oneElement = true
            }

            db.collection("saloes").document("salao ${salonId}")
                .collection("obras").document(selectedArt.name)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "${selectedArt.name} foi deletado com sucesso!", Toast.LENGTH_SHORT).show()
                    loadArtsFromFirestore()

                    if (oneElement) {
                        artsList.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
        }
    }

    override fun onEditIconClick(position: Int) {
        val selectedArt = artsList[position]
        val docRef = db.collection("saloes").document("salao ${salonId}")
            .collection("obras").document(selectedArt.name)

        val outsideCard: FrameLayout = findViewById(R.id.art_creation_background) // Layout do card
        val leaveButton: RelativeLayout = findViewById(R.id.leave_card) // Botão de sair do card
        val errorPrevention: FrameLayout = findViewById(R.id.error_prevention_background) // Card de prevenção de erros

        outsideCard.visibility = View.VISIBLE

        val artName: EditText = findViewById(R.id.art_name)
        val artYear: EditText = findViewById(R.id.art_year)
        val artAuthor: EditText = findViewById(R.id.art_author)
        val artDescription: EditText = findViewById(R.id.art_description)

        getFieldFromDatabase(artName, "Nome da obra", docRef)
        getFieldFromDatabase(artYear, "Ano", docRef)
        getFieldFromDatabase(artAuthor, "Autor", docRef)
        getFieldFromDatabase(artDescription, "Descrição", docRef)

        addImage = findViewById(R.id.add_image)
        showImageFromDatabase(addImage, docRef)

        val editArt: Button = findViewById(R.id.create_art)
        editArt.text = "Editar Obra"

        configEditTextToOnlyInteger(artYear)

        // Abre a galeria do celular e seleciona imagem
        addImage.setOnClickListener {
            openGallery()
        }

        editArt.setOnClickListener {
            // Adiciona número e imagem, fazer if (se não tiver imagem ou texto, não adicionar)
            if (addImage.drawable != null && (artName.text.toString() != "" || artYear.text.toString() != ""
                        || artAuthor.text.toString() != "" || artDescription.text.toString() != "")) {
                val art = mapOf(
                    "Ano" to artYear.text.toString(),
                    "Autor" to artAuthor.text.toString(),
                    "Descrição" to artDescription,
                    "Nome da obra" to artName.text.toString(),
                    "imagem" to imageViewToBase64(addImage)
                )

                docRef.update(art)
                    .addOnSuccessListener {
                        Toast.makeText(this, "A obra ${selectedArt.name} foi atualizada com sucesso", Toast.LENGTH_SHORT).show()
                        // Opcional: Atualize a lista e o adaptador se necessário
                        loadArtsFromFirestore()

                        outsideCard.visibility = View.GONE
                        artName.text.clear()
                        artYear.text.clear()
                        artAuthor.text.clear()
                        artDescription.text.clear()

                        addImage.setImageDrawable(null)
                    }
            }  else {
                Toast.makeText(this, "A obra está sem imagem ou algum campo não foi preenchido!", Toast.LENGTH_SHORT).show()
            }
        }

        leaveButton.setOnClickListener {
            // Prevenção de erros
            val errorPreventionText: TextView = findViewById(R.id.error_prevention_text)
            errorPreventionText.text = "Você tem certeza que deseja sair da edição da obra?"
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

            artName.text.clear()
            artYear.text.clear()
            artAuthor.text.clear()
            artDescription.text.clear()
            addImage.setImageDrawable(null)
        }
    }

    private fun getFieldFromDatabase(input: EditText, field: String, dbRef: DocumentReference) {
        dbRef.get().addOnSuccessListener {
            if (it != null) {
                input.setText(it.getString(field))
            } else {
                Log.d("Database", "No such document")
            }
        }
    }

    private fun showImageFromDatabase(image: ImageView, dbRef: DocumentReference) {
        dbRef.get().addOnSuccessListener {
            if (it != null) {
                val imageInString = it.getString("imagem")
                image.setImageDrawable(imageInString?.let { it1 -> decodeBase64ToDrawable(it1) })
            } else {
                Log.d("Database", "No such document")
            }
        }
    }
}