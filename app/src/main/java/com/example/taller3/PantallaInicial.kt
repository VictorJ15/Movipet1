package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class PantallaInicial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mostrarPantallaDeBienvenida(4000)
    }

    private fun mostrarPantallaDeBienvenida(tiempoDeEspera: Long) {

        val handler = Handler()

        val runnable = Runnable {
            setContentView(R.layout.pantalla_inicial)
            handler.postDelayed({
                val intent = Intent(this, LobbyInicial::class.java)
                startActivity(intent)
            }, tiempoDeEspera)
        }
        handler.postDelayed(runnable, 500)
    }
}
