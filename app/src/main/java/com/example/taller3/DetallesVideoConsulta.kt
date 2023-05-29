package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DetallesVideoConsulta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_consulta)
        val btn = findViewById<Button>(R.id.pagarvideoConsulta)
        btn.setOnClickListener{
            val intent = Intent(this, Pago::class.java)
            startActivity(intent)


        }
    }
}