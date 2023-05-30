package com.example.taller3

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3.databinding.RegistroDatosUsuarioBinding
import com.example.taller3.databinding.RegistroDatosVeterinarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class RegistroDatosVeterinario : AppCompatActivity() {

    private lateinit var binding: RegistroDatosVeterinarioBinding
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegistroDatosVeterinarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnContinuar.setOnClickListener {
            registroVeterinario(binding.etEmailVeterinario.text.toString(), binding.etPasswordVeterinario.text.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun registroVeterinario (email: String, password:String){
        if (validateForm()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
                        val user = auth.currentUser
                        if (user != null) {
                            val upcrb = UserProfileChangeRequest.Builder()
                            upcrb.displayName = binding.etNombreVeterinario.text.toString() + " " + binding.etApellidoVeterinario.text.toString()
                            upcrb.photoUri = Uri.parse("path/to/pic") //fake uri, use Firebase Storage
                            user.updateProfile(upcrb.build())
                            updateUI(user)
                            cargarVeterinarioFireBase()
                        }
                    } else {
                        Toast.makeText(
                            this, "createUserWithEmail:Failure: " + task.exception.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        task.exception?.message?.let { Log.e(TAG, it) }
                    }
                }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@") ||
            !email.contains(".") ||
            email.length < 5)
            return false
        return true
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = binding.etEmailVeterinario.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.etEmailVeterinario.error = "Este campo es obligatorio."
            valid = false
        } else {
            isEmailValid(email)
            binding.etEmailVeterinario.error = null
        }
        val password = binding.etPasswordVeterinario.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.etPasswordVeterinario.error = "Este campo es obligatorio."
            valid = false
        } else {
            binding.etPasswordVeterinario.error = null
        }
        return valid
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, RegistroFotoVeterinario::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            binding.etEmailVeterinario.setText("")
            binding.etPasswordVeterinario.setText("")
        }
    }

    private fun cargarVeterinarioFireBase (){

        val mRootReference = FirebaseDatabase.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()

        val nombreVeterinario = binding.etNombreVeterinario.text.toString()
        val apellidoVeterinario = binding.etApellidoVeterinario.text.toString()
        val correoVeterinario = binding.etEmailVeterinario.text.toString()
        val passwordVeterinario = binding.etPasswordVeterinario.text.toString()
        val placaVehiculo = binding.etMarcaVehiculo.text.toString()
        val latitudVeterinario = binding.etLatitud.text.toString()
        val longitudVeterinario = binding.etLongitud.text.toString()


        val nuevoVeterinario  = HashMap<String, Any>()
        nuevoVeterinario["nombre"] = nombreVeterinario
        nuevoVeterinario["apellido"] = apellidoVeterinario
        nuevoVeterinario["correo"] = correoVeterinario
        nuevoVeterinario["password"] = passwordVeterinario
        nuevoVeterinario["foto"] = "pendiente"
        nuevoVeterinario["estado"] = "pendiente"
        nuevoVeterinario["latitud"] = latitudVeterinario
        nuevoVeterinario["longitud"] = longitudVeterinario
        nuevoVeterinario["placa del vehiculo"] = placaVehiculo


        mRootReference.reference.child("veterinarios").child(firebaseAuth.currentUser!!.uid).setValue(nuevoVeterinario)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Veterinario registrado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al registrar el veterinario", Toast.LENGTH_SHORT).show()
                }
            }
    }
}