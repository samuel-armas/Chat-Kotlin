package com.example.practica1

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practica1.databinding.ActivityCambiarPasswordBinding
import com.example.practica1.databinding.ActivityOlvidePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CambiarPassword : BaseActivity() {
    private lateinit var binding: ActivityCambiarPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCambiarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarInsets(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        progressDialog = ProgressDialog (this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCambiarPass.setOnClickListener {
            validarInformacion()
        }
        
    }

    private var pass_actual = ""
    private var pass_nueva = ""
    private var r_pass_nueva = ""
    private fun validarInformacion() {
        pass_actual = binding.etPassActual.text.toString().trim()
        pass_nueva = binding.etPassNueva.text.toString().trim()
        r_pass_nueva = binding.etRPassNueva.text.toString().trim()



        if (pass_actual.isEmpty()){
            binding.etPassActual.error = "Ingrese contraseña actual"
            binding.etPassActual.requestFocus()
        }
        else if (pass_nueva.isEmpty()){
            binding.etPassNueva.error = "Ingrese contraseña nueva"
            binding.etPassNueva.requestFocus()
        }
        else if (r_pass_nueva.isEmpty()) {
            binding.etRPassNueva.error = "Repita contraseña nueva"
            binding.etRPassNueva.requestFocus()
        }
        else if (pass_nueva!= r_pass_nueva){
            binding.etRPassNueva.error = "Repita contraseña nueva"
            binding.etRPassNueva.requestFocus()
        }
        else {
            autenticarUsuario()
        }
    }
    private fun autenticarUsuario() {
        progressDialog.setMessage("Autenticando usuario")
        progressDialog.show()

        val authCredential = EmailAuthProvider.getCredential(firebaseUser.email.toString(), pass_actual)
        firebaseUser.reauthenticate((authCredential))
            .addOnSuccessListener {
                actualizarPassword()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Falló la autenticación debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun actualizarPassword() {
        progressDialog.setMessage("Cambiando contraseña")
        progressDialog.show()

        firebaseUser.updatePassword(pass_nueva)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "La contraseña se ha actualizado",
                    Toast.LENGTH_SHORT
                ).show()
                cerraSesion()
            }

            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Falló al actualizar la contraseña debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun cerraSesion() {
        firebaseAuth.signOut()
        startActivity(Intent(applicationContext, OpcionesLoginActivity::class.java))
        finishAffinity()
    }
}






