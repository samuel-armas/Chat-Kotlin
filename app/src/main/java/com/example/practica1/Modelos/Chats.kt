package com.example.practica1.Modelos

class Chats {

    var imagen : String = ""
    var nombres : String = ""
    var keyChat : String = ""
    var uidRecibimos : String = ""
    var idMensaje : String = ""
    var tipoMensaje : String = ""
    var mensaje : String = ""
    var emisorUid : String = ""
    var receptorUid : String = ""
    var tiempo : Long = 0

    constructor()
    constructor(
        imagen: String,
        nombres: String,
        keyChat: String,
        uidRecibimos: String,
        idMensaje: String,
        tipoMensaje: String,
        mensaje: String,
        emisorUid: String,
        receptorUid: String,
        tiempo: Long
    ) {
        this.imagen = imagen
        this.nombres = nombres
        this.keyChat = keyChat
        this.uidRecibimos = uidRecibimos
        this.idMensaje = idMensaje
        this.tipoMensaje = tipoMensaje
        this.mensaje = mensaje
        this.emisorUid = emisorUid
        this.receptorUid = receptorUid
        this.tiempo = tiempo
    }


}