package net.azarquiel.foro2019.model

import java.io.Serializable

data class Respuesta(
    val user:Usuario,
    val msg:String,
    val temas:List<Tema>,
    val tema:Tema,
    val comentario:Comentario,
    val comentarios:List<Comentario>
)

data class Tema(
    var _id:Int,
    var descripcion:String
):Serializable

data class Avatar(var avatar: String)

data class Comentario(
    var telefono:String,
    var nick:String,
    var avatar:String,
    var _id:Int,
    var fecha:String,
    var post:String
)

data class Usuario(
    var telefono:String,
    var nick:String,
    var avatar:String
):Serializable