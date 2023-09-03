package com.berktavli.fotografpaylasmafirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.berktavli.fotografpaylasmafirebase.databinding.ActivityHaberlerBinding
import com.google.firebase.auth.FirebaseAuth

class HaberlerActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityHaberlerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHaberlerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()




    }




    /*    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val menuInflater = menuInflater
            menuInflater.inflate(R.menu.options_menu,menu)
            return super.onCreateOptionsMenu(menu)
        }



        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == R.id.fotograf_paylas){
                //fotograf paylaşma ekranına gidilecek
                val intent = Intent(this@HaberlerActivity,FotografPaylasmaActivity::class.java)
                startActivity(intent)
                //finish() burada finish cagırmaya  gerek yok, fotograf paylasmaktan vazgeçip geri dönmesini sağlamak amacıyla.
            }else if (item.itemId == R.id.cikis_yap){
                //firebase'e çıkış yaptığımızı bildirmemiz lazım, guncelKullanici
                // null gelsin ve giriş ekranını doldurmamız gereksin
                auth.signOut()//firebase'den çıkış yapıyor.
                val intent = Intent(this@HaberlerActivity,KullaniciActivity::class.java)
                startActivity(intent)
                finish()
            }

            return super.onOptionsItemSelected(item)
        }


        **************************setOnClickListener*****************
        val fotografPaylasmaButonu = binding.addPhoto
        val kullaniciCikis = binding.exitSession


        fotografPaylasmaButonu.setOnClickListener{
            val intent = Intent(this@HaberlerActivity,FotografPaylasmaActivity::class.java)
            startActivity(intent)
        }

        kullaniciCikis.setOnClickListener {
            auth.signOut()//firebase'den çıkış yapıyor.
            val intent = Intent(this@HaberlerActivity, KullaniciActivity::class.java)
            startActivity(intent)
            finish()
        }


     */


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