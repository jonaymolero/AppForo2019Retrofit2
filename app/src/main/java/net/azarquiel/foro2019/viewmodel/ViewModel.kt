package net.azarquiel.foro2019.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.azarquiel.foro2019.api.MainRepository
import net.azarquiel.foro2019.model.Avatar
import net.azarquiel.foro2019.model.Comentario
import net.azarquiel.foro2019.model.Tema
import net.azarquiel.foro2019.model.Usuario

class ViewModel : ViewModel() {

    private var repository: MainRepository =
        MainRepository()

    fun getTemas(): MutableLiveData<List<Tema>> {
        val dataTemas = MutableLiveData<List<Tema>>()
        GlobalScope.launch(Main) {
            dataTemas.value = repository.getTemas()
        }
        return dataTemas
    }

    fun getComentarios(idTema:Int): MutableLiveData<List<Comentario>>{
        val dataComentarios = MutableLiveData<List<Comentario>>()
        GlobalScope.launch(Main) {
            dataComentarios.value = repository.getComentarios(idTema)
        }
        return dataComentarios
    }

    fun getUsuario(telefono:String):MutableLiveData<Usuario>{
        val usario= MutableLiveData<Usuario>()
        GlobalScope.launch(Main) {
            usario.value = repository.getUsuario(telefono)
        }
        return usario
    }

    fun saveTema(tema:Tema):MutableLiveData<Tema>{
        val temaResponse= MutableLiveData<Tema>()
        GlobalScope.launch(Main) {
            temaResponse.value = repository.saveTema(tema)
        }
        return temaResponse
    }

    fun saveComentario(idTema: Int,comentario:Comentario):MutableLiveData<Comentario>{
        val comentarioResponse= MutableLiveData<Comentario>()
        GlobalScope.launch(Main) {
            comentarioResponse.value = repository.saveComentario(idTema,comentario)
        }
        return comentarioResponse
    }

    fun saveUsuario(usuario:Usuario):MutableLiveData<String>{
        val response= MutableLiveData<String>()
        GlobalScope.launch(Main) {
            response.value = repository.saveUsuario(usuario)
        }
        return response
    }

    fun saveAvatar(telefono: String,avatar: Avatar):MutableLiveData<String>{
        val response= MutableLiveData<String>()
        GlobalScope.launch(Main) {
            response.value = repository.saveAvatar(telefono, avatar)
        }
        return response
    }
}