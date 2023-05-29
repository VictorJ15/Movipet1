package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InfoVetVideo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_vet_video)
        val btn = findViewById<Button>(R.id.soliVetvi)
        btn.setOnClickListener {
            val intent = Intent(this, DetallesVideoConsulta::class.java)
            startActivity(intent)

        }
        val btn2 = findViewById<Button>(R.id.cancelarsoliVideo)
        btn2.setOnClickListener {
            val intent = Intent(this, ServicioCancelado::class.java)
            startActivity(intent)
        }
    }}