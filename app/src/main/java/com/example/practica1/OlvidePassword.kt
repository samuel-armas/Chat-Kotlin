package com.example.practica1

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica1.databinding.ActivityOlvidePasswordBinding
import com.google.firebase.auth.FirebaseAuth

class OlvidePassword : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityOlvidePasswordBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOlvidePasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEnviar.setOnClickListener {
            validarInformacion()

        }
    }


    private var email = ""
    private fun validarInformacion() {
        email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()){
            binding.etEmail.error = "Ingrese su email"
            binding.etEmail.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no valido"
            binding.etEmail.requestFocus()
        }
        else {
            enviarInstrucciones()
        }

    }
    private fun enviarInstrucciones(){
        progressDialog.setMessage("Enviando las instrucciones a $email")
        progressDialog.show()


        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Intrucciones enviadas",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Falló el envio de instrucciones debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }


}


