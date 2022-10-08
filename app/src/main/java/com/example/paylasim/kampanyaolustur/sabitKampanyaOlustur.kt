package com.example.paylasim.kampanyaolustur

import android.Manifest
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.paylasim.R
import com.example.paylasim.home.MainActivity
import com.example.paylasim.models.sabitKampanya
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sabit_kampanya_olustur.*
import java.util.*

class sabitKampanyaOlustur : AppCompatActivity() {

    var secilengorsel: Uri? = null
    var secilenbitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sabit_kampanya_olustur)

        db= FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        auth= Firebase.auth
    }




    fun paylasimyap(view: View) {






        val uuid = UUID.randomUUID()
        val gorselismi = "${uuid}.jpg"
        val reference = storage.reference
        val gorselreference = reference.child("sabitKampanyalar").child(gorselismi)


        if (secilengorsel!=null){
            gorselreference.putFile(secilengorsel!!).addOnSuccessListener { taskSnapshot->
                val yuklenengorselreference=
                    FirebaseStorage.getInstance().reference.child("sabitKampanyalar").child(gorselismi)
                yuklenengorselreference.downloadUrl.addOnSuccessListener { uri->
                    val downloadurl=uri.toString()
                    veritabaninakaydet(downloadurl)


                }.addOnFailureListener {  exception->
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()

                }



            }



        }else{
            Toast.makeText(this,"Lütfen Fotoğraf Yükleyiniz",Toast.LENGTH_LONG).show()




        }


    }

    private fun veritabaninakaydet(downloadurl:String?){




        var postID = db.child("sabitKampanyalar").child(auth.uid!!).push().key
        var yuklenenPost = sabitKampanya(auth.uid, postID, 0,aciklama_idd.text.toString(), downloadurl)



        db.child("sabitKampanyalar").child(auth.uid!!).child(postID!!).setValue(yuklenenPost)
        db.child("sabitKampanyalar").child(auth.uid!!).child(postID).child("yuklenme_tarih").setValue(ServerValue.TIMESTAMP)
        Toast.makeText(this,"Kampanya Oluşturuldu",Toast.LENGTH_LONG).show()




        val intent=Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()


    }




    fun gorselsec(view: View){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)


        }else{
            val galeriintent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriintent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                val galeriintent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriintent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2&& resultCode== Activity.RESULT_OK&& data!=null){
            secilengorsel= data.data
            if(secilengorsel!=null){
                if (Build.VERSION.SDK_INT>=28){
                    val source= ImageDecoder.createSource(this.contentResolver,secilengorsel!!)
                    secilenbitmap= ImageDecoder.decodeBitmap(source)
                    gorselllsec.setImageBitmap(secilenbitmap)

                }else{
                    secilenbitmap=
                        MediaStore.Images.Media.getBitmap(this.contentResolver,secilengorsel)
                    gorselllsec.setImageBitmap(secilenbitmap) }


            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}