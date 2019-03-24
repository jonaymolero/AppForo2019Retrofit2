package net.azarquiel.foro2019.api

import net.azarquiel.foro2019.model.Avatar
import net.azarquiel.foro2019.model.Comentario
import net.azarquiel.foro2019.model.Tema
import net.azarquiel.foro2019.model.Usuario

class MainRepository {
    val service = WebAccess.foroService

    suspend fun getTemas(): List<Tema>{
        val webResponse = service.getTemas().await()
        if (webResponse.isSuccessful) {
            return webResponse.body()!!.temas
        }
        return emptyList()
    }

    suspend fun getComentarios(idTema:Int): List<Comentario>{
        val webResponse = service.getComentariosDeUnTema(idTema).await()
        if (webResponse.isSuccessful) {
            return webResponse.body()!!.comentarios
        }
        return emptyList()
    }

    suspend fun getUsuario(telefono:String):Usuario?{
        var usuario:Usuario?=null
        val webResponse = service.getUsuarioTelefono(telefono).await()
        if (webResponse.isSuccessful) {
            usuario = webResponse.body()!!.user
        }
        return usuario
    }

    suspend fun saveTema(tema: Tema): Tema?{
        var temaResponse:Tema?=null
        val webResponse = service.saveTema(tema).await()
        if (webResponse.isSuccessful) {
            temaResponse = webResponse.body()!!.tema
        }
        return temaResponse
    }

    suspend fun saveComentario(idTema: Int,comentario:Comentario):Comentario?{
        var comentarioResponse:Comentario?=null
        val webResponse = service.saveComentario(idTema,comentario).await()
        if (webResponse.isSuccessful) {
            comentarioResponse = webResponse.body()!!.comentario
        }
        return comentarioResponse
    }

    suspend fun saveUsuario(usuario:Usuario):String?{
        var response:String?=null
        val webResponse = service.saveUsuario(usuario).await()
        if (webResponse.isSuccessful) {
            response = webResponse.body()!!.msg
        }
        return response
    }

    suspend fun saveAvatar(telefono: String,avatar: Avatar):String?{
        var response:String?=null
        val webResponse = service.saveAvatar(telefono, avatar).await()
        if (webResponse.isSuccessful) {
            response = webResponse.body()!!.msg
        }
        return response
    }
}