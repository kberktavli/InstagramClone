package com.berktavli.fotografpaylasmafirebase.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.berktavli.fotografpaylasmafirebase.databinding.ActivityFotografPaylasmaBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.util.UUID

class FotografPaylasmaActivity : AppCompatActivity() {



    private lateinit var binding: ActivityFotografPaylasmaBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var imageData : Uri? = null
    var selectedBitmap : Bitmap? = null

    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotografPaylasmaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        registerLauncher()

        storage = FirebaseStorage.getInstance() // Firebase.storage
        auth = FirebaseAuth.getInstance()//Firebase.auth
        database = FirebaseFirestore.getInstance()//Firebase.firestore

    }

    fun upload(view : View){
        //depo işlemleri
        //UUID -> universal unique id
        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.jpg"
        println("yüklendi3")
        val reference = storage.reference

        val gorselReference = reference.child("images").child(gorselIsmi)

        if (imageData != null){
            println("yüklendi1")
            gorselReference.putFile(imageData!!).addOnSuccessListener{
                 val yuklenenGorselReference = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener {uri->
                    val downloadUrl = uri.toString()
                    val guncelKullaniciEmaili = auth.currentUser!!.email.toString()
                    val kullaniciYorumu = binding.yorumText.text.toString()
                    val tarih = com.google.firebase.Timestamp.now()
                    //veritabanı işlemleri
                    // hashmap olusturuyoruz

                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("gorselurl",downloadUrl)
                    postHashMap.put("kullaniciemail",guncelKullaniciEmaili)
                    postHashMap.put("kullaniciyorum",kullaniciYorumu)
                    postHashMap.put("tarih",tarih)

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            finish()//fotografpaylasmaactivity kapatıp haberler ekranına geri dönücek
                        }
                    }.addOnFailureListener { exception->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }
            }.addOnFailureListener {exception->
                println("Yükleme Sırasında hata olustu")//yüklenmeme hatası alırsan console'a gidip güvenlik kurallarından okuma yazmaya auth null degilse izin ver.
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }

    }

    fun selectImage(view: View){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            // ANDROİD 33+ -> READ_MEDİA_IMAGES
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //rational
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission Needed For Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //Request Permission işlemini yapacagız
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
                }else{
                    //Eğer rational'i göstermek istemiyorsak yine Request Permission yapacagız
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }
            }else{
                //İzni halı hazırda aldıysak, galeriye intent yapacagız//intent
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)

            }
        }else{
            //Android 32- ->READ_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //rational
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission Needed For Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //Request Permission işlemini yapacagız
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }else{
                    //Eğer rational'i göstermek istemiyorsak yine Request Permission yapacagız
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }
            }else{
                //İzni halı hazırda aldıysak, galeriye intent yapacagız//intent
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)

            }
        }






    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null){
                     imageData = intentFromResult.data
                    if (imageData != null){
                        try {
                            if (Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(this@FotografPaylasmaActivity.contentResolver,imageData!!)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@FotografPaylasmaActivity,"Permission Needed!!!!!!",Toast.LENGTH_LONG).show()
            }

        }
    }


}

//Request-Permission ve intent yapabilmek için ActivityResultLauncher isimli sınıftan faydalanmamız
//lazım, on create altında initialize etmemiz lazım
