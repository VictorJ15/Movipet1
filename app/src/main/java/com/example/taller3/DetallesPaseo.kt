package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class DetallesPaseo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalles_paseo)
        val btn = findViewById<Button>(R.id.pagarPaseador)
        btn.setOnClickListener{
            val intent = Intent(this, Pago::class.java)
            startActivity(intent)
        }
    }
}