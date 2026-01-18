package com.example.practica1

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica1.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class LoginEmailActivity : AppCompatActivity() {

    private lateinit var binging : ActivityLoginEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binging = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binging.root)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binging.btnIngresar.setOnClickListener {
            validarInformacion()
        }
        //UN EVENTO DE CLICK PARA DIRIGIRNOS DE UN LADO A OTRO, DANDO CLICK
        binging.tvRecuperarCuenta.setOnClickListener {
            startActivity(Intent(applicationContext, OlvidePassword::class.java))
        }

        binging.tvRegistrarme.setOnClickListener {
            startActivity(Intent(applicationContext, RegistroEmailActivity::class.java))
        }
    }

    private var email = ""
    private var password = ""
    private fun validarInformacion() {
        email = binging.etEmail.text.toString().trim()
        password = binging.etPassword.text.toString().trim()

        if (email.isEmpty()){
            binging.etEmail.error = "Ingrese el email"
            binging.etEmail.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binging.etEmail.error = "Email no valido"
            binging.etEmail.requestFocus()
        }
        if (password.isEmpty()){
            binging.etPassword.error = "Ingrese la contraseña"
            binging.etPassword.requestFocus()
        }
        else {
            loguearUsuario()
        }
    }

    private fun loguearUsuario() {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)

            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se realizó el logueo debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}


