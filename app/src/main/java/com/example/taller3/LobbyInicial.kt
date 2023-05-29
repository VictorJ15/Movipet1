package com.example.taller3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3.databinding.LobbyInicialBinding


class LobbyInicial : AppCompatActivity() {

    private lateinit var binding: LobbyInicialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LobbyInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVeterinario.setOnClickListener {
            val intent = Intent(this, LoginVeterinario::class.java)
            startActivity(intent)
        }

        binding.btnUsuario.setOnClickListener {
            val intent = Intent(this, LoginUsuario::class.java)
            startActivity(intent)
        }
    }
}