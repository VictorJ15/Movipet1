package com.example.taller3
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.taller3.databinding.LoginUsuarioBinding
import com.example.taller3.databinding.LoginVeterinarioBinding


class LoginVeterinario : AppCompatActivity() {

    private lateinit var binding: LoginVeterinarioBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginVeterinarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            signInUser(binding.etEmailVeterinario.text.toString(), binding.etPasswordVeterinario.text.toString())
        }

        binding.btnRegistro.setOnClickListener {
            val intent = Intent(this, RegistroDatosVeterinario::class.java)
            startActivity(intent)
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


    private fun signInUser (email: String, password: String) {
        if (validateForm()){
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "Inicio de sesion exitoso")
                    Toast . makeText ( baseContext , "Inicio de sesion exitoso." , Toast . LENGTH_SHORT ). show ()
                    val user = auth.currentUser
                    updateUI(user)
                }else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "Error al iniciar sesion", task.exception)
                    Toast.makeText(
                        baseContext, "Error al iniciar sesion.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
        }
    }

    // Verifica si el usuario ya accedio a la aplicacion
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, MapaGeneral::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            binding.etEmailVeterinario.setText("")
            binding.etPasswordVeterinario.setText("")
        }
    }
}