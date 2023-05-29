package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Pago : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pago)
        val btn = findViewById<Button>(R.id.fin)
        btn.setOnClickListener{
            val intent = Intent(this, Exitoso::class.java)
            startActivity(intent)
        }
    }
}