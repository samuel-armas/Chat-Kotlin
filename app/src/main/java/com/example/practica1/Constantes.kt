package com.example.practica1

import android.icu.text.DateFormat
import android.icu.util.Calendar
import java.util.Arrays
import java.util.Locale

object Constantes {

    const val MENSAJE_TIPO_TEXTO = "TEXTO"
    const val MENSAJE_TIPO_IMAGEN = "IMAGEN"

    fun obtenerTiempoD() : Long {
        return System.currentTimeMillis()
    }

    fun formatoFecha (tiempo : Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = tiempo

        return android.text.format.DateFormat.format("dd/MM/yyyy", calendar.time).toString()
    }

    fun obtenerFechaHora (tiempo: Long) : String{
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = tiempo

        return android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar.time).toString()
    }

    fun rutaChat (receptorUid : String, emisorUid : String) : String{
        val arrayUid = arrayOf(receptorUid, emisorUid)
        Arrays.sort(arrayUid)
        return "${arrayUid[0]}_${arrayUid[1]}"
    }
}