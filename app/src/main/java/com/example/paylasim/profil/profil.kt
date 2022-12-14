package com.example.paylasim.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasim.R
import com.example.paylasim.models.kampanya
import com.example.paylasim.models.kullaniciKampanya
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.util.EventbusData
import com.example.paylasim.util.imageLoader
import com.example.paylasim.util.profilActivityRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.android.synthetic.main.activity_profil_ayarlar.tv_Mesaj
import org.greenrobot.eventbus.EventBus


class profil : AppCompatActivity() {
     lateinit var mref:DatabaseReference
     lateinit var muser:FirebaseUser
    var tumGonderiler=ArrayList<kullaniciKampanya>()
    private lateinit var recyclerviewadapter:profilActivityRecyclerAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)


        mref=FirebaseDatabase.getInstance().reference
        muser= FirebaseAuth.getInstance().currentUser!!


        kullaniciBilgileriVerileriniAl()
        profilDuzenle()
        verileriGetir(muser.uid)


        //******************** KONTROL ET**************************
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));





    }


    private fun verileriGetir(kullanicid: String) {



        mref.child("users").child("isletmeler").child(kullanicid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var userID=kullanicid
                var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture

                mref.child("kampanya").child(kullanicid).addListenerForSingleValueEvent(object :ValueEventListener{
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

                                Log.e("tomprofil","sayisi:"+tumGonderiler.size)


                            }

                        }
                        val layoutManager= LinearLayoutManager(this@profil)
                        recyclerProfil.layoutManager=layoutManager
                        recyclerviewadapter= profilActivityRecyclerAdapter(this@profil,tumGonderiler)
                        recyclerProfil.adapter=recyclerviewadapter



                    }

                    override fun onCancelled(error: DatabaseError) {

                    }


                })

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        mref.child("users").child("kullanicilar").child(kullanicid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot!!.getValue()!=null){

                    var userID=kullanicid
                    var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                    var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture

                    mref.child("kampanya").child(kullanicid).addListenerForSingleValueEvent(object :ValueEventListener{
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
                            val layoutManager= LinearLayoutManager(this@profil)
                            recyclerProfil.layoutManager=layoutManager
                            recyclerviewadapter= profilActivityRecyclerAdapter(this@profil,tumGonderiler)
                            recyclerProfil.adapter=recyclerviewadapter



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
        recyclerProfilContainer.visibility= View.VISIBLE
        profilFragmentContainer.visibility= View.GONE
        super.onBackPressed()
    }


    private fun kullaniciBilgileriVerileriniAl() {

        tv_Mesaj.isEnabled=false

        mref.child("users").child("isletmeler").child(muser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.getValue()!=null){
                    var okunanKullanici=snapshot!!.getValue(kullanicilar::class.java)
                    EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                    tv_Mesaj.isEnabled=true

                    tv_kullaniciAdi
                        .setText(okunanKullanici!!.user_name)
                    tv_post.setText(okunanKullanici!!.user_detail!!.post)
                    if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                        tv_bio.setText(okunanKullanici!!.user_detail!!.biography)


                    }

                    var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                    imageLoader.setImage(imgUrl,profile_image,null,"")


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        mref.child("users").child("kullanicilar").child(muser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot!!.getValue()!=null){
                    var okunanKullanici=snapshot!!.getValue(kullanicilar::class.java)
                    EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                    tv_Mesaj.isEnabled=true

                    tv_kullaniciAdi.setText(okunanKullanici!!.user_name)
                      tv_post.setText(okunanKullanici!!.user_detail!!.post)
                    if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                        tv_bio.setText(okunanKullanici!!.user_detail!!.biography)


                    }

                    var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                    imageLoader.setImage(imgUrl,profile_image,null,"")


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun profilDuzenle(){
        tv_Mesaj.setOnClickListener(){
            val intent= Intent(this,profilAyarlarActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
    }
}