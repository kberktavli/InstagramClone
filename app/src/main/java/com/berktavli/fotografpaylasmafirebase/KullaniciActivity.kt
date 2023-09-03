package com.berktavli.fotografpaylasmafirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.berktavli.fotografpaylasmafirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class KullaniciActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        /*Kullanıcı daha önceden kayıt olmuş ve giriş yapmışsa , onCreate'de bunu kontrol edip
        tekrar gmail ve password sormadan direk kullanıcıyı ana feed'e aktarıyorum.
        Bunu yaptıktan sonra log out işlemi yapmamız lazım'ki kullanıcı değişikliği yapılabilsin.
         */
        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null){
            val intent = Intent(this@KullaniciActivity,HaberlerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun girisYap(view:View){
        //sign in
        auth.signInWithEmailAndPassword(binding.emailText.text.toString(),binding.passwordText.text.toString()).addOnCompleteListener { task->
            if (task.isSuccessful){
                val guncelKullanici = auth.currentUser?.email.toString()
                Toast.makeText(this@KullaniciActivity,"Hosgeldin: ${guncelKullanici}",Toast.LENGTH_LONG).show()
                val intent = Intent(this@KullaniciActivity,HaberlerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception->
            Toast.makeText(this@KullaniciActivity,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }
    fun kayitOl(view:View){
        //sign up

        val email = binding.emailText.text.toString()
        val sifre = binding.passwordText.text.toString()

        auth.createUserWithEmailAndPassword(email,sifre).addOnCompleteListener { task ->
            //asenkron
            if (task.isSuccessful){
                //diger aktiviteye gidelim
                val intent = Intent(this@KullaniciActivity,HaberlerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{exception->
           Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }

    }
}