package com.example.practica1.Adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.practica1.Chat.ChatActivity
import com.example.practica1.Modelos.Usuario
import com.example.practica1.R

class AdaptadorUsuario(
    context : Context,
    listaUsuarios : List<Usuario>) : RecyclerView.Adapter<AdaptadorUsuario.ViewHolder?> (){

        private val context : Context
        private val listaUsuarios : List <Usuario>

        init {
            this.context = context
            this.listaUsuarios = listaUsuarios
        }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario : Usuario = listaUsuarios[position]
        holder.uid.text = usuario.uid
        holder.email.text = usuario.email
        holder.nombres.text = usuario.nombres
        // IMAGEN USANDO BASE 64
        val imagenBase64 = usuario.imagen
        if (!imagenBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.imagen.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.imagen.setImageResource(R.drawable.ic_img_perfil) // imagen por defecto
            }
        } else {
            holder.imagen.setImageResource(R.drawable.ic_img_perfil)
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("uid",holder.uid.text)
            Toast.makeText(context, "Has seleccionado al usuario : ${holder.nombres.text}", Toast.LENGTH_SHORT).show()
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var uid : TextView
        var email : TextView
        var nombres : TextView
        var imagen : ImageView

        init {
            uid = itemView.findViewById(R.id.item_uid)
            email = itemView.findViewById(R.id.item_email)
            nombres = itemView.findViewById(R.id.item_nombre)
            imagen = itemView.findViewById(R.id.item_imagen)
        }
    }
}