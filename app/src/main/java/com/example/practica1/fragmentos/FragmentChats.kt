package com.example.practica1.fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.practica1.Adaptadores.AdaptadorChat
import com.example.practica1.Adaptadores.AdaptadorChats
import com.example.practica1.Modelos.Chats
import com.example.practica1.Modelos.Chat
import com.example.practica1.R
import com.example.practica1.databinding.FragmentChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentChats : Fragment() {

    private lateinit var binding: FragmentChatsBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private var miUid = ""

    private lateinit var chatsArrayList : ArrayList<Chats>

    private lateinit var adaptadorChats: AdaptadorChats

    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth= FirebaseAuth.getInstance()
        miUid = "${firebaseAuth.uid}"

        cargarChats()
    }

    private fun cargarChats() {
        chatsArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatsArrayList.clear()
                for(ds in snapshot.children){
                    val chatKey = "${ds.key}"
                    if (chatKey.contains(miUid)){
                        val modeloChats = Chats()
                        modeloChats.keyChat = chatKey
                        chatsArrayList.add(modeloChats)
                    }
                }

                adaptadorChats = AdaptadorChats(mContext, chatsArrayList)
                binding.chatsRv.adapter = adaptadorChats
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}