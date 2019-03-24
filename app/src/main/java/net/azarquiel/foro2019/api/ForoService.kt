package net.azarquiel.foro2019.api

import kotlinx.coroutines.Deferred
import net.azarquiel.foro2019.model.*
import retrofit2.Response
import retrofit2.http.*

interface ForoService {
    // todos los temas
    @GET("temas")
    fun getTemas(): Deferred<Response<Respuesta>>

    // todos los comentarios de un tema
    @GET("tema/{tema}/comentarios")
    fun getComentariosDeUnTema(@Path("tema") tema: Int): Deferred<Response<Respuesta>>

    //trae el user con el telefono
    @GET("user/{telefono}")
    fun getUsuarioTelefono(@Path("telefono") telefono: String): Deferred<Response<Respuesta>>

    // post de un user
    @POST("user")
    fun saveUsuario(@Body usuario: Usuario): Deferred<Response<Respuesta>>

    @POST("tema")
    fun saveTema(@Body tema: Tema): Deferred<Response<Respuesta>>

    // post de un comentario a un tema
    @POST("tema/{tema}/comentario")
    fun saveComentario(@Path("tema") tema: Int,
                   @Body comentario: Comentario): Deferred<Response<Respuesta>>

    //post de un avatar para un usario
    @POST("user/{telefono}/avatar")
    fun saveAvatar(@Path("telefono") telefono: String,
                   @Body avatar: Avatar): Deferred<Response<Respuesta>>
}