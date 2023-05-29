package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class DetallesServicio : AppCompatActivity() {

    private lateinit var nombreVeterinario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detalles_servicio)

        val btn = findViewById<Button>(R.id.btn_continuar)
        btn.setOnClickListener{
            val intent = Intent(this, Pago::class.java)
            startActivity(intent)
        }

        // Obtener el nombre y apellido del veterinario de los extras del intent
        nombreVeterinario = intent.getStringExtra("nombreVeterinario").toString()

        // Obtener una referencia a la ubicación de los veterinarios en la base de datos
        val mRootReference = FirebaseDatabase.getInstance().reference.child("veterinarios")

        // Consultar la base de datos para obtener los datos del veterinario por nombre y apellido
        val query = mRootReference.orderByChild("nombre").equalTo(nombreVeterinario)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Verificar si se encontraron resultados
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {

                        // Obtener los datos del veterinario
                        val nombre = snapshot.child("nombre").getValue(String::class.java)
                        val apellido = snapshot.child("apellido").getValue(String::class.java)
                        val placa = snapshot.child("placa del vehiculo").getValue(String::class.java)
                        val fotoVeterinario = snapshot.child("foto").getValue(String::class.java)

                        // Mostrar los datos en la interfaz de usuario (por ejemplo, en TextViews o ImageViews)
                        val nombreVeterinario = findViewById<TextView>(R.id.et_nombreVeterinario)
                        nombreVeterinario.text = nombre

                        val apellidoVeterinario = findViewById<TextView>(R.id.et_apellidoVeterinario)
                        apellidoVeterinario.text = apellido

                        val placaCarro = findViewById<TextView>(R.id.et_placaVehiculo)
                        placaCarro.text = placa


                        break // Si solo deseas obtener un veterinario, puedes detener el bucle después de obtener los datos del primer resultado
                    }
                } else {
                    Toast.makeText(this@DetallesServicio, "No se encontraron datos del veterinario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@DetallesServicio, "Error al consultar la base de datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
