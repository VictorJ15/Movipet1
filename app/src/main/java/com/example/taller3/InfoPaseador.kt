package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InfoPaseador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_paseador)
        val btn = findViewById<Button>(R.id.contPaseador)
        btn.setOnClickListener {
            val intent = Intent(this, MapaGeneral::class.java)
            startActivity(intent)

        }
        val btn2 = findViewById<Button>(R.id.cancelarsoliPaseador)
        btn2.setOnClickListener {
            val intent = Intent(this, ServicioCancelado::class.java)
            startActivity(intent)
        }
    }}