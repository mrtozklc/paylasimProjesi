package com.example.paylasim.kampanyaolustur

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.paylasim.R
import com.example.paylasim.home.MainActivity
import com.example.paylasim.models.kampanya
import com.example.paylasim.util.mainActivityRecyclerAdapter
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_kampanya_olustur.*
import kotlinx.android.synthetic.main.activity_kampanya_olustur.view.*
import kotlinx.android.synthetic.main.recycler_row.*
import java.util.*

class kampanyaOlustur : AppCompatActivity() {
    var secilengorsel: Uri? = null
    var secilenbitmap: Bitmap? = null
    var secilenSure:String?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var db:DatabaseReference
    private lateinit var auth: FirebaseAuth





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kampanya_olustur)

        db= FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        auth=Firebase.auth

        val timer = ArrayList<String>()
        timer.add("Geri Sayım Süresi Seciniz")
        timer.add("1 saat")
        timer.add("2 saat")
        timer.add("3 saat")












 val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, timer)
 spinner!!.setAdapter(spinnerAdapter)


 spinner!!.setOnItemSelectedListener(object :AdapterView.OnItemSelectedListener{
     override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

       secilenSure = spinner!!.selectedItem.toString()

     }

     override fun onNothingSelected(p0: AdapterView<*>?) {
     }

 })




}



    fun paylasimyap(view: View) {


 btn_paylas.isEnabled=false
        Log.e("TAG","tıklandı. ${btn_paylas.isEnabled}")


 secilenSure = spinner!!.selectedItem.toString()

 val intent=Intent(this,mainActivityRecyclerAdapter::class.java)
 intent.putExtra("time", secilenSure)



 val uuid = UUID.randomUUID()
 val gorselismi = "${uuid}.jpg"
 val reference = storage.reference
 val gorselreference = reference.child("images").child(gorselismi)


 if (secilengorsel!=null){
     gorselreference.putFile(secilengorsel!!).addOnSuccessListener { taskSnapshot->
         val yuklenengorselreference=
             FirebaseStorage.getInstance().reference.child("images").child(gorselismi)
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




 var postID = db.child("kampanya").child(auth.uid!!).push().key
 var yuklenenPost = kampanya(auth.uid, postID, 0,aciklama_id.text.toString(),secilenSure, downloadurl)



 db.child("kampanya").child(auth.uid!!).child(postID!!).setValue(yuklenenPost)
 db.child("kampanya").child(auth.uid!!).child(postID).child("yuklenme_tarih").setValue(ServerValue.TIMESTAMP)
 Toast.makeText(this,"Kampanya Oluşturuldu",Toast.LENGTH_LONG).show()


 if(!aciklama_id.text.toString().isNullOrEmpty()){

     db.child("yorumlar").child(postID).child(postID).child("user_id").setValue(auth.uid)
     db.child("yorumlar").child(postID).child(postID).child("yorum_tarih").setValue(ServerValue.TIMESTAMP)
     db.child("yorumlar").child(postID).child(postID).child("yorum").setValue(aciklama_id.text.toString())
     db.child("yorumlar").child(postID).child(postID).child("yorum_begeni").setValue("0")

 }
 kampanyaSayisiniGuncelle()

 val intent=Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
 startActivity(intent)
 finish()


}

private fun kampanyaSayisiniGuncelle() {

 db.child("users").child("isletmeler").child(auth.uid!!).child("user_detail").addListenerForSingleValueEvent(object :ValueEventListener{
     override fun onDataChange(snapshot: DataSnapshot) {
         var oankiGonderiSayisi=snapshot!!.child("post").getValue().toString().toInt()
         oankiGonderiSayisi++
         db.child("users").child("isletmeler").child(auth.uid!!).child("user_detail").child("post").setValue(oankiGonderiSayisi.toString())
     }

     override fun onCancelled(error: DatabaseError) {
     }

 })
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
             imageView4.setImageBitmap(secilenbitmap)

         }else{
             secilenbitmap=
                 MediaStore.Images.Media.getBitmap(this.contentResolver,secilengorsel)
             imageView4.setImageBitmap(secilenbitmap) }


     }

 }
 super.onActivityResult(requestCode, resultCode, data)
}
}