package com.example.paylasim.profil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasim.R
import com.example.paylasim.mesajlar.chat
import com.example.paylasim.models.kampanya
import com.example.paylasim.models.kullaniciKampanya
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.util.EventbusData
import com.example.paylasim.util.imageLoader
import com.example.paylasim.util.userProfilRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.activity_profil.tv_Mesaj
import kotlinx.android.synthetic.main.activity_user_profil.*
import org.greenrobot.eventbus.EventBus

class userProfil : AppCompatActivity() {

    lateinit var mref: DatabaseReference
    lateinit var muser: FirebaseUser
    var tumGonderiler=ArrayList<kullaniciKampanya>()
    lateinit var secilenUser:String
    private lateinit var recyclerviewadapter: userProfilRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profil)





        mref= FirebaseDatabase.getInstance().reference
        muser= FirebaseAuth.getInstance().currentUser!!
        secilenUser= intent.getStringExtra("secilenUserId")!!


        kullaniciBilgileriVerileriniAl(secilenUser)
        profilDuzenle()
        verileriGetir(secilenUser)


        //******************** KONTROL ET**************************
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));


    }
    private fun verileriGetir(kullanicid: String) {


        mref.child("users").child("isletmeler").child(kullanicid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var userID=kullanicid
                var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture

                mref.child("kampanya").child(kullanicid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot!!.hasChildren()){

                            for (ds in snapshot!!.children){
                                var eklenecekUserPost= kullaniciKampanya()
                                eklenecekUserPost.userID=userID
                                eklenecekUserPost.userName=kullaniciadi
                                eklenecekUserPost.userPhotoURL=photoURL
                                eklenecekUserPost.postID=ds.getValue(kampanya::class.java)!!.post_id
                                eklenecekUserPost.postURL=ds.getValue(kampanya::class.java)!!.file_url
                                eklenecekUserPost.postAciklama=ds.getValue(kampanya::class.java)!!.aciklama
                                eklenecekUserPost.postYuklenmeTarih=ds.getValue(kampanya::class.java)!!.yuklenme_tarih

                                tumGonderiler.add(eklenecekUserPost)


                                Log.e("gelengonderiler","tum gonderiler"+tumGonderiler.size)







                            }

                        }
                        val layoutManager= LinearLayoutManager(this@userProfil)
                        recyclerUserProfil.layoutManager=layoutManager
                        recyclerviewadapter=userProfilRecyclerAdapter(this@userProfil,tumGonderiler)
                        recyclerUserProfil.adapter=recyclerviewadapter



                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        mref.child("users").child("kullanicilar").child(kullanicid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)  {
                if (snapshot!!.getValue()!=null){
                    var userID=kullanicid
                    var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                    var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture

                    mref.child("kampanya").child(kullanicid).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.hasChildren()){

                                for (ds in snapshot!!.children){
                                    var eklenecekUserPost= kullaniciKampanya()
                                    eklenecekUserPost.userID=userID
                                    eklenecekUserPost.userName=kullaniciadi
                                    eklenecekUserPost.userPhotoURL=photoURL
                                    eklenecekUserPost.postID=ds.getValue(kampanya::class.java)!!.post_id
                                    eklenecekUserPost.postURL=ds.getValue(kampanya::class.java)!!.file_url
                                    eklenecekUserPost.postAciklama=ds.getValue(kampanya::class.java)!!.aciklama
                                    eklenecekUserPost.postYuklenmeTarih=ds.getValue(kampanya::class.java)!!.yuklenme_tarih

                                    tumGonderiler.add(eklenecekUserPost)







                                }

                            }
                            val layoutManager= LinearLayoutManager(this@userProfil)
                            recyclerUserProfil.layoutManager=layoutManager
                            recyclerviewadapter=userProfilRecyclerAdapter(this@userProfil,tumGonderiler)
                            recyclerUserProfil.adapter=recyclerviewadapter



                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

                }



            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    override fun onBackPressed() {
        recyclerUserProfilContainer.visibility= View.VISIBLE
        userProfilFragmentContainer.visibility= View.GONE
        super.onBackPressed()
    }


    private fun kullaniciBilgileriVerileriniAl(kullanicid: String) {

        tv_Mesaj.isEnabled=false

        mref.child("users").child("isletmeler").child(kullanicid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.getValue()!=null){
                    var okunanKullanici=snapshot!!.getValue(kullanicilar::class.java)
                    EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                    tv_Mesaj.isEnabled=true

                    tv_kullaniciAdii.setText(okunanKullanici!!.user_name)
                    tv_postt.setText(okunanKullanici!!.user_detail!!.post)
                    if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                        tv_bioo.setText(okunanKullanici!!.user_detail!!.biography)


                    }

                    var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                    imageLoader.setImage(imgUrl,profile_imagee,null,"")


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        mref.child("users").child("kullanicilar").child(kullanicid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.getValue()!=null){
                    var okunanKullanici=snapshot!!.getValue(kullanicilar::class.java)
                    EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                    tv_Mesaj.isEnabled=true

                    tv_kullaniciAdii.setText(okunanKullanici!!.user_name)
                    tv_postt.setText(okunanKullanici!!.user_detail!!.post)
                    if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                        tv_bioo.setText(okunanKullanici!!.user_detail!!.biography)


                    }

                    var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                    imageLoader.setImage(imgUrl,profile_imagee,null,"")


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun profilDuzenle(){
        tv_Mesaj.setOnClickListener(){
            val intent= Intent(this,chat::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("konusulacakKisi",secilenUser)


            startActivity(intent)
        }
    }
}