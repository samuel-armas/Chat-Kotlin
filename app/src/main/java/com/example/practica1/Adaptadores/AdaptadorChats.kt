package com.example.practica1.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.credentials.webauthn.Cbor
import androidx.recyclerview.widget.RecyclerView
import com.example.practica1.Chat.ChatActivity
import com.example.practica1.Modelos.Chats
import com.example.practica1.Constantes
import com.example.practica1.databinding.ItemChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.view.View
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import kotlin.coroutines.Continuation
import com.example.practica1.R


class AdaptadorChats : RecyclerView.Adapter<AdaptadorChats.HolderChats>{
    private var context : Context
    private var chatArrayList : ArrayList<Chats>
    private lateinit var binding : ItemChatsBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private var miUid = ""

    constructor(context: Context, chatArrayList: ArrayList<Chats>) {
        this.context = context
        this.chatArrayList = chatArrayList
        firebaseAuth = FirebaseAuth.getInstance()
        miUid = firebaseAuth.uid!!
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HolderChats {
        binding = ItemChatsBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderChats(binding.root)
    }

    override fun onBindViewHolder(holder: HolderChats, position: Int) {
        val modeloChats = chatArrayList[position]

        cargarUltimoMensaje(modeloChats, holder)

        holder.itemView.setOnClickListener {
            val uidRecibimos = modeloChats.uidRecibimos
            if (uidRecibimos!=null){
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("uid", uidRecibimos)
                context.startActivity(intent)
            }
        }

    }

    private fun cargarUltimoMensaje( modeloChats: Chats, holder: HolderChats) {
        val chatKey = modeloChats.keyChat
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatKey).limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val emisorUid = "${ds.child("emisorUid").value}"
                        val idMensaje = "${ds.child("idMensaje").value}"
                        val mensaje = "${ds.child("mensaje").value}"
                        val receptorUid = "${ds.child("receptorUid").value}"
                        val tiempo = ds.child("tiempo").value as Long
                        val tipoMensaje = "${ds.child("tipoMensaje").value}"


                        val formatoFechaHora = Constantes.obtenerFechaHora(tiempo)
                        modeloChats.emisorUid = emisorUid
                        modeloChats.idMensaje = idMensaje
                        modeloChats.mensaje = mensaje
                        modeloChats.receptorUid = receptorUid
                        modeloChats.tipoMensaje = tipoMensaje

                        holder.tvFecha.text = "$formatoFechaHora"

                        if (tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
                            holder.tvUltimoMensaje.text = mensaje
                        }
                        else {
                            holder.tvUltimoMensaje.text = "Se ha enviado una imagen"
                        }

                        cargarInfoUsuarioRecibido(modeloChats, holder)

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    override fun getItemCount(): Int {
        return chatArrayList.size
    }
    private fun cargarInfoUsuarioRecibido( modeloChats: Chats, holder: HolderChats) {
        val emisorUid = modeloChats.emisorUid
        val receptorUid = modeloChats.receptorUid

        var uidRecibimos = ""
        if(emisorUid==miUid){
            uidRecibimos =receptorUid
        }else {
            uidRecibimos = emisorUid
        }

        modeloChats.uidRecibimos = uidRecibimos

        val refBadge = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(miUid)
            .child("noLeidos")
            .child(uidRecibimos)
            .child("count")

        refBadge.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.getValue(Int::class.java) ?: 0

                if (count > 0) {
                    holder.txtBadge.visibility = android.view.View.VISIBLE
                    holder.txtBadge.text = count.toString()
                } else {
                    holder.txtBadge.visibility = android.view.View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidRecibimos)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    modeloChats.nombres = nombres
                    modeloChats.imagen = imagen

                    holder.tvNombres.text = nombres
                    try {
                        if (!imagen.isNullOrEmpty()) {
                            val imageBytes = Base64.decode(imagen, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            holder.IvPerfil.setImageBitmap(bitmap)   // usa el ImageView correcto de tu Holder
                        } else {
                            holder.IvPerfil.setImageResource(R.drawable.ic_img_perfil) // placeholder
                        }
                    } catch (e: Exception) {
                        holder.IvPerfil.setImageResource(R.drawable.ic_img_perfil) // fallback en caso de error
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    inner class HolderChats (itemView: android.view.View) : RecyclerView.ViewHolder(itemView){
        var IvPerfil = binding.IvPerfil
        var tvNombres = binding.tvNombres
        var tvUltimoMensaje = binding.tvUltimoMensaje
        var tvFecha = binding.tvFecha
        var txtBadge = binding.txtBadge
    }
}