package com.example.taller3

import android.content.Intent
import android.os.Bundle
import com.squareup.picasso.Picasso
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.example.taller3.databinding.DetallesServicioBinding


class DetallesServicio : AppCompatActivity() {

    private lateinit var idVeterinario: String
    private lateinit var binding:DetallesServicioBinding
    private lateinit var distanciaS : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetallesServicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val picasso = Picasso.get()


// Obtener el id del veterinario de los extras del intent
        idVeterinario = intent.getStringExtra("idVeterinario").toString()
        distanciaS = intent.getStringExtra("distanciaS").toString()

        binding.btnContinuar.setOnClickListener {
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
                picasso.load(fotoVeterinario).into(binding.img)
                binding .etPrecioServicio.text =calcularPrecio().toString()


            }else{
                Toast.makeText(this,"No existe este id",Toast.LENGTH_SHORT).show()
            }





        }.addOnFailureListener{
            Toast.makeText(this,"Fallo lectura de Base de Datos",Toast.LENGTH_SHORT).show()
        }

    }
    private fun calcularPrecio(): Double? {
        val valorPorKilometro = 185.71 // Cambiar el valor según tus necesidades
        val double1: Double? = distanciaS.toDouble()
        val precio = double1?.times(valorPorKilometro)
        val tarifaVeterinario = 35000
        val totalPagar = precio?.plus(tarifaVeterinario)
        return totalPagar
    }


}