package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class ConsultaVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consulta_video)
        mostrarsiguiente(4000)

    }
    private fun mostrarsiguiente(tiempoDeEspera: Long) {

        val handler = Handler()

        val runnable = Runnable {
            setContentView(R.layout.consulta_video)
            handler.postDelayed({
                val intent = Intent(this, InfoVetVideo::class.java)
                startActivity(intent)
            }, tiempoDeEspera)
        }
        handler.postDelayed(runnable, 500)
    }
}