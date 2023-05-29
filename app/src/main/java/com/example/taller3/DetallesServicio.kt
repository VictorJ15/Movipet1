package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.example.taller3.databinding.DetallesServicioBinding

class DetallesServicio : AppCompatActivity() {

    private lateinit var idVeterinario: String
    private lateinit var binding:DetallesServicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetallesServicioBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtener el id del veterinario de los extras del intent
        idVeterinario = intent.getStringExtra("idVeterinario").toString()

        binding.btn_continuar.setOnClickListener {
            val intent = Intent(this, Pago::class.java)
            startActivity(intent)
        }




        // Obtener una referencia a la ubicación de los veterinarios en la base de datos
        val mRootReference = FirebaseDatabase.getInstance().reference.child("veterinarios")

        // Consultar la base de datos para obtener los datos del veterinario por nombre y apellido
        val query = mRootReference.orderByChild("nombre").equalTo(idVeterinario)
        mRootReference.child(idVeterinario).get().addOnSuccessListener {
            if(it.exists()){

                val nombre = it.child("nombre").getValue(String::class.java)
                val apellido = it.child("apellido").getValue(String::class.java)
                val placa = it.child("placa del vehiculo").getValue(String::class.java)
                val fotoVeterinario = it.child("foto").getValue(String::class.java)
                binding.etNombreVeterinario.text = nombre
                binding.etApellidoVeterinario.text = apellido
                binding.etPlacaVehiculo.text = placa


            }else{
                Toast.makeText(this,"No existe este id",Toast.LENGTH_SHORT).show()
            }





        }.addOnFailureListener{
            Toast.makeText(this,"Fallo lectura de Base de Datos",Toast.LENGTH_SHORT).show()
        }

        /*query.addListenerForSingleValueEvent(object : ValueEventListener {
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
        })*/


    }
}
