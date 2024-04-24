package com.example.activationnfcglao

import android.app.PendingIntent
import android.content.IntentFilter
import android.nfc.Tag
import android.os.Bundle
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var counter = 0


        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Comprueba si el modo avión está activado
        if (Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0) {
            // Modo avión está activado, muestra un Toast y navega a la configuración de modo avión
            Toast.makeText(
                this,
                "Modo avión está activado. Desactívalo en Configuración.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS))
        } else {
           counter ++
            // Inicialización del adaptador NFC
            nfcAdapter = NfcAdapter.getDefaultAdapter(this).also { adapter ->
                if (adapter == null) {
                    Toast.makeText(this, "Este dispositivo no soporta NFC.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    if (!adapter.isEnabled) {
                        // NFC está desactivado, muestra un Toast y navega a la configuración de NFC
                        Toast.makeText(
                            this,
                            "NFC está desactivado. Actívalo en Configuración.",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                    } else {
                        counter ++
                        // NFC está activado, muestra un Toast
                        //Toast.makeText(this, "NFC está activado.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            // Comprueba si los datos móviles están activados
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            if (telephonyManager.dataState == TelephonyManager.DATA_DISCONNECTED) {
                // Datos móviles están desactivados, muestra un Toast y navega a la configuración de datos móviles
                Toast.makeText(
                    this,
                    "Datos móviles están desactivados. Actívalos en Configuración.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }else {
                counter ++
            }

        }



        onDestroy() // Llama al método onDestroy para finalizar la actividad
/*
        if (counter == 3){

ANY
            val packageName = "com.ngi.movilgmao" // Reemplaza con el nombre del paquete de la aplicación que deseas lanzar
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                // La aplicación no está instalada, maneja la situación como prefieras
            }


        }

 */
        counter =0

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tagFromIntent = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        // Aquí puedes manejar el tag NFC
    }

    override fun onResume() {
        super.onResume()
        // Configura el foreground dispatch para que la actividad capture los intents NFC
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val intentFiltersArray = arrayOf<IntentFilter>()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Llama a finish() para finalizar la actividad
        finish()
    }
}
