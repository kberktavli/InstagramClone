package com.berktavli.fotografpaylasmafirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.berktavli.fotografpaylasmafirebase.databinding.ActivityHaberlerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HaberlerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityHaberlerBinding
    private lateinit var database : FirebaseFirestore

    var postListesi = ArrayList<Post>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHaberlerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        fetchData()
    }
    fun fetchData(){
        database.collection("Post").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null){
                Toast.makeText(this@HaberlerActivity,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (snapshot!=null){
                    //snapshot null olmayabilir ama içinde birşey olmayabilir
                    if (!snapshot.isEmpty) {
                       val documents = snapshot.documents
                        postListesi.clear()
                        for (document in documents){
                            val kullaniciEmail = document.get("kullaniciemail") as String
                            val kullaniciYorumu = document.get("kullaniciyorumu") as String
                            val gorselUrl = document.get("gorselurl") as String

                            val indirilenPost = Post(kullaniciEmail,kullaniciYorumu,gorselUrl)
                            postListesi.add(indirilenPost)
                        }
                    }
                }
            }
        }
    }





    fun addPhoto(view : View) {
        val intent = Intent(this@HaberlerActivity,FotografPaylasmaActivity::class.java)
        startActivity(intent)
    }

    fun exitApp(view : View) {

        auth.signOut()//firebase'den çıkış yapıyor.
        val intent = Intent(this@HaberlerActivity, KullaniciActivity::class.java)
        startActivity(intent)
        finish()
    }







}