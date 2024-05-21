package com.example.espacocultural

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.espacocultural.models.GlobalVariables
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.camera.CameraSettings

class QrPage : AppCompatActivity() {

    private var isCameraActive = true
    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.qr_page)

        // Inicializa a visualização da câmera
        barcodeView = DecoratedBarcodeView(this)
        barcodeView.decodeContinuous(callback)

        // Adiciona a visualização da câmera ao contêiner
        val cameraContainer: FrameLayout = findViewById(R.id.camera_preview)
        cameraContainer.addView(
            barcodeView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Botão ativar/desativar câmera
        val toggleCamera: Button = findViewById(R.id.toggle_camera_button)

        toggleCamera.setOnClickListener {
            toggleCamera(toggleCamera)
        }

        // Botões barra inferior
        val homeButton = findViewById<Button>(R.id.homeButton)
        val compassButton = findViewById<Button>(R.id.compassButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        homeButton.setOnClickListener{
            changeScreen(this, HomePage::class.java)
        }

        compassButton.setOnClickListener{
            changeScreen(this, SalonsPage::class.java)
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

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let {
                // Processa o resultado do escaneamento do QR code aqui
                //val intent = Intent(this@QrPage, ArtInfoPage::class.java)
                //intent.putExtra("qrCodeContent", it.text)
                //startActivity(intent)
                changeScreen(this@QrPage, ArtInfoPage::class.java)
            }
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
            // Se necessário, lide com os pontos de resultado possível aqui
        }
    }

    override fun onResume() {
        super.onResume()
        // Inicia a câmera quando a atividade é retomada
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        // Pausa a câmera quando a atividade é pausada para liberar recursos
        barcodeView.pause()
    }

    fun toggleCamera(button: Button) {
        if (isCameraActive) {
            barcodeView.pause()
            val cameraContainer: FrameLayout = findViewById(R.id.camera_preview)
            cameraContainer.removeView(barcodeView)
            button.setText(R.string.camera_on)
        } else {
            val cameraContainer: FrameLayout = findViewById(R.id.camera_preview)
            cameraContainer.addView(
                barcodeView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            barcodeView.resume()
            button.setText(R.string.camera_off)
        }
        isCameraActive = !isCameraActive
    }
}