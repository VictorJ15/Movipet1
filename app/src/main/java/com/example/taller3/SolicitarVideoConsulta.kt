package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SolicitarVideoConsulta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solicitar_video_consulta)
        val btn = findViewById<Button>(R.id.solicitarVideoConsul)
        btn.setOnClickListener {
            val intent = Intent(this, ConsultaVideo::class.java)
            startActivity(intent)
        }
    }
}