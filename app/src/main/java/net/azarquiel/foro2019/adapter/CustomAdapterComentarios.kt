package net.azarquiel.foro2019.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rowcomentarios.view.*
import net.azarquiel.foro2019.R
import net.azarquiel.foro2019.model.Comentario

class CustomAdapterComentarios(val context: Context,
                    val layout: Int
                    ) : RecyclerView.Adapter<CustomAdapterComentarios.ViewHolder>() {

    private var dataList: List<Comentario> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setComentarios(comentarios: List<Comentario>) {
        this.dataList = comentarios
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: Comentario){
            itemView.tvNombreComentario.text=dataItem.post
            itemView.tvNombreUsuario.text=dataItem.nick
            itemView.tvFecha.text=dataItem.fecha
            if(dataItem.avatar!=""){
                Glide.with(context).load("http://www.ies-azarquiel.es/paco/apiforo/resources/avatar/${dataItem.avatar}").into(itemView.ivAvatarUsuario)
            }else{
                itemView.ivAvatarUsuario.setImageResource(R.drawable.user)
            }
        }
    }
}