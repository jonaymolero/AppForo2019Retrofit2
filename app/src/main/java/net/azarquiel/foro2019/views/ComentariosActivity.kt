package net.azarquiel.foro2019.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager.LayoutParams.*
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_comentarios.*
import kotlinx.android.synthetic.main.content_comentarios.*
import net.azarquiel.foro2019.R
import net.azarquiel.foro2019.adapter.CustomAdapterComentarios
import net.azarquiel.foro2019.model.Comentario
import net.azarquiel.foro2019.model.Tema
import net.azarquiel.foro2019.model.Usuario
import net.azarquiel.foro2019.viewmodel.ViewModel
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*

class ComentariosActivity : AppCompatActivity() {

    private lateinit var tema:Tema
    private lateinit var usuario:Usuario
    private lateinit var viewModel: ViewModel
    private lateinit var adapter:CustomAdapterComentarios
    private lateinit var post: EditText
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentarios)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { addComentario()}
        swipeRefreshLayout=refrescar
        tema=intent.getSerializableExtra("temaPulsado") as Tema
        usuario=intent.getSerializableExtra("usuario") as Usuario
        title=tema.descripcion
        construirAdapter()
        dameComentarios()
        swipeRefreshLayout.setOnRefreshListener {
            dameComentarios()
            swipeRefreshLayout.isRefreshing=false
        }
    }

    private fun construirAdapter(){
        adapter= CustomAdapterComentarios(this,R.layout.rowcomentarios)
        rvComentarios.layoutManager = LinearLayoutManager(this)
        rvComentarios.adapter=adapter
    }

    private fun dameComentarios(){
        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        viewModel.getComentarios(tema._id).observe(this, Observer {
            //adapter.setBares(it!!) // with nullable
            //it?.let{adapter.setBares(it)} // unwrap nullable it
            it?.let(adapter::setComentarios)  // to lambda
        })
    }

    private fun addComentario() {
        alert("Introduce el post al tema ${tema.descripcion}") {
            positiveButton("Añadir Comentario"){añadirComentario()}
            negativeButton("Cancelar"){}
            customView {
                verticalLayout{
                    post=editText {
                        hint = getString(R.string.post)
                    }
                }
            }
        }.show()
    }

    private fun añadirComentario() {
        if(post.text.isEmpty()){
            toast("Introduce una descripcion")
            addComentario()
        }else{
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            if(usuario.avatar!=null){
                viewModel.saveComentario(tema._id,Comentario(usuario.telefono,usuario.nick,usuario.avatar,tema._id,date.format(Date()),"${post.text}"))
            }else{
                viewModel.saveComentario(tema._id,Comentario(usuario.telefono,usuario.nick,"",tema._id,date.format(Date()),"${post.text}"))
            }
        }
    }
}
