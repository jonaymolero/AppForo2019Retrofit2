package net.azarquiel.foro2019.views

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.robertlevonyan.components.picker.ItemModel
import com.robertlevonyan.components.picker.PickerDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import net.azarquiel.foro2019.R
import net.azarquiel.foro2019.adapter.CustomAdapter
import net.azarquiel.foro2019.model.Avatar
import net.azarquiel.foro2019.model.Tema
import net.azarquiel.foro2019.model.Usuario
import net.azarquiel.foro2019.viewmodel.ViewModel
import org.jetbrains.anko.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel: ViewModel
    private lateinit var temas: ArrayList<Tema>
    private lateinit var adapter: CustomAdapter
    private lateinit var descripcion: EditText
    private lateinit var telefono: EditText
    private lateinit var nick: EditText
    private var usuario: Usuario?=null
    private lateinit var ivAvatar:CircleImageView
    private lateinit var nickAvatar:TextView
    private lateinit var preferencias:SharedPreferences
    private lateinit var pickerDialog: PickerDialog
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {addTema()}

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        swipeRefreshLayout=refrescarTemas
        ivAvatar=nav_view.getHeaderView(0).ivAvatar
        nickAvatar=nav_view.getHeaderView(0).nickAvatar
        ivAvatar.setOnClickListener {
            picker()
        }

        showBar()
        dameTemas()
        buscarUsuario()
        swipeRefreshLayout.setOnRefreshListener {
            dameTemas()
            swipeRefreshLayout.isRefreshing=false
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_register -> {
                registrarUsuario()
            }
            R.id.nav_login->{
                login()
            }
            R.id.nav_logout->{
                logout()
            }
            R.id.nav_salir -> {
                finish()
            }
            R.id.nav_info -> {
                acercade()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun picker() {
        if (usuario==null) {
            toast("Login or Register please....")
            return
        }
        val itemModelc = ItemModel(ItemModel.ITEM_CAMERA)
        val itemModelg = ItemModel(ItemModel.ITEM_GALLERY)
        pickerDialog = PickerDialog.Builder(this)
            .setListType(PickerDialog.TYPE_GRID)
            .setItems(arrayListOf(itemModelg, itemModelc))
            .setDialogStyle(PickerDialog.DIALOG_MATERIAL)
            .create()

        pickerDialog.setPickerCloseListener { type, uri ->
            when (type) {
                ItemModel.ITEM_CAMERA -> {
                    subirAvatar(uri)
                }
                ItemModel.ITEM_GALLERY -> {
                    subirAvatar(uri)
                }
            }
        }

        pickerDialog.show(supportFragmentManager, "")
    }

    private fun subirAvatar(uri : Uri) {
        val bitmapavatar: Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        val avatar = Avatar(encodeToBase64(bitmapavatar, Bitmap.CompressFormat.JPEG, 100))
        viewModel.saveAvatar(usuario!!.telefono,avatar).observe(this, Observer { it ->
            it?.let {
                usuario!!.avatar = it
                guardarUsuarioSP()
                ivAvatar.setImageBitmap(bitmapavatar)
            }
        })
    }

    private fun encodeToBase64(image: Bitmap, compressFormat: Bitmap.CompressFormat, quality: Int): String {
        val byteArrayOS = ByteArrayOutputStream()
        image.compress(compressFormat, quality, byteArrayOS)
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
    }


    private fun dameTemas(){
        viewModel = ViewModelProviders.of(this).get(ViewModel::class.java)
        viewModel.getTemas().observe(this, Observer {
            //adapter.setBares(it!!) // with nullable
            //it?.let{adapter.setBares(it)} // unwrap nullable it
            it?.let(adapter::setTemas)  // to lambda
        })
    }

    private fun showBar() {
        adapter = CustomAdapter(this, R.layout.rowtemas)
        rvTemas.layoutManager = LinearLayoutManager(this)
        rvTemas.adapter = adapter
    }

    private fun buscarUsuario() {
        preferencias= getSharedPreferences("usuarios", Context.MODE_PRIVATE)
        val user = preferencias.getString("user","nosta")
        if(user!="nosta"){
            usuario= Gson().fromJson(user, Usuario::class.java)
            pintarUsuario()
        }
    }

    private fun guardarUsuarioSP(){
        val jsonUsuario: String = Gson().toJson(usuario)
        val editor = preferencias.edit()
        editor.putString("user", jsonUsuario)
        editor.commit()
    }

    private fun eliminarUsuarioSP(){
        val editor = preferencias.edit()
        editor.remove("user")
        editor.commit()
    }

    private fun addTema() {
        alert("Introduce la descripción del tema") {
            positiveButton("Añadir tema"){añadirTema()}
            negativeButton("Cancelar"){}
            customView {
                verticalLayout{
                    descripcion=editText {
                        hint = getString(R.string.descripcion)
                    }
                }
            }
        }.show()
    }

    private fun añadirTema(){
        if(descripcion.text.isEmpty()){
            toast("Introduce una descripcion")
            addTema()
        }else{
            viewModel.saveTema(Tema(1, "${descripcion.text}"))
        }
    }

    private fun registrarUsuario(){
        alert("Introduce telefono y nick") {
            positiveButton("Aceptar"){comprobarUsuario()}
            negativeButton("Cancelar"){}
            customView {
                verticalLayout{
                    telefono=editText {
                        hint = getString(R.string.telefono)
                    }
                    nick=editText {
                        hint = getString(R.string.nick)
                    }
                }
            }
        }.show()
    }

    private fun login(){
        alert("Introduce telefono y nick") {
            positiveButton("Aceptar"){comprobarUsuarioLogin()}
            negativeButton("Cancelar"){}
            customView {
                verticalLayout{
                    telefono=editText {
                        hint = getString(R.string.telefono)
                    }
                }
            }
        }.show()
    }

    private fun comprobarUsuario(){
        if(telefono.text.isEmpty() && nick.text.isEmpty()){
            toast("Introduce telefono y nick")
            registrarUsuario()
        }else{
            usuario= Usuario("${telefono.text}","${nick.text}","")
            viewModel.saveUsuario(usuario!!).observe(this, Observer {
                if(it=="Ok"){
                    guardarUsuarioSP()
                    pintarUsuario()
                    toast("Registrado correctamente")
                }else{
                    toast("Error al registrar")
                }
            })
        }
    }

    private fun comprobarUsuarioLogin(){
        if(telefono.text.isEmpty()){
            toast("Introduce telefono")
            login()
        }else{
            viewModel.getUsuario("${telefono.text}").observe(this, Observer {
                if(it!=null){
                    usuario=it
                    guardarUsuarioSP()
                    pintarUsuario()
                    toast("Bienvenido ${usuario!!.nick}")
                }else{
                    toast("El usuario no existe")
                }
            })
        }
    }

    private fun logout() {
        if(usuario==null){
            toast("No estabas logeado")
        }else{
            toast("Hasta luego ${usuario!!.nick}")
            usuario=null
            eliminarUsuarioSP()
            pintarUsuario()
        }
    }

    private fun pintarUsuario(){
        this.nickAvatar.text="Usuario"
        this.ivAvatar.setImageResource(R.drawable.user)

        if(usuario!=null){
            this.nickAvatar.text=usuario!!.nick
            if(usuario!!.avatar!=null && usuario!!.avatar!=""){
                Glide.with(this).load("http://www.ies-azarquiel.es/paco/apiforo/resources/avatar/${usuario!!.avatar}").into(this.ivAvatar)
            }
        }
    }

    private fun acercade() {
        alert("IES Azarquiel") {
            yesButton {  }
        }.show()
    }

    fun clickTema(v: View){
        if(usuario!=null){
            var tema=v.tag as Tema
            var intent= Intent(this,ComentariosActivity::class.java)
            intent.putExtra("temaPulsado", tema)
            intent.putExtra("usuario",usuario)
            startActivity(intent)
        }else{
            toast("Tienes que logearte")
        }
    }
}
