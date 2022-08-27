package com.example.paylasim.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasim.R
import com.example.paylasim.kampanyaolustur.kampanyaOlustur
import com.example.paylasim.login.LoginActivity
import com.example.paylasim.login.signOutFragment
import com.example.paylasim.mesajlar.mesajlar
import com.example.paylasim.models.kampanya
import com.example.paylasim.models.kullaniciKampanya
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.profil.profil
import com.example.paylasim.util.mainActivityRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
     var tumGonderiler=ArrayList<kullaniciKampanya>()
    var tumPostlar=ArrayList<String>()
    private lateinit var recyclerviewadapter:mainActivityRecyclerAdapter


    @SuppressLint("UseSupportActionBar")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar(findViewById(R.id.my_toolbar))
        my_toolbar.inflateMenu(R.menu.menu)

        auth=Firebase.auth
        mref = FirebaseDatabase.getInstance().reference


        setupAuthLis()
        tumVerileriGetir()









    }

    override fun onBackPressed() {
       recyclerMainContainer.visibility= View.VISIBLE
      mainFragmentContainer.visibility= View.GONE
        super.onBackPressed()
    }

    private fun tumVerileriGetir(){

        mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.getValue()!=null){
                    for(ds in p0.children){
                        tumPostlar.add(ds.key!!)
                    }
                    verileriGetir()
                }
            }


        })

    }


    private fun verileriGetir() {

        mref=FirebaseDatabase.getInstance().reference
        for(i in 0..tumPostlar.size-1){
            var kullaniciID = tumPostlar.get(i)

            mref.child("users").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var userID=kullaniciID
                    var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                    var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture

                    mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.hasChildren()){

                                for (ds in snapshot!!.children){
                                    var eklenecekUserPost=kullaniciKampanya()
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
                            if(i>=tumPostlar.size-1){
                                val layoutManager= LinearLayoutManager(this@MainActivity)
                                recyclerAnaSayfa.layoutManager=layoutManager
                                recyclerviewadapter= mainActivityRecyclerAdapter(this@MainActivity,tumGonderiler)
                                recyclerAnaSayfa.adapter=recyclerviewadapter

                            }




                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }




    }








    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.cikisYap){
            var dialog=signOutFragment()
            dialog.show(supportFragmentManager,"Çıkış yap")




        }
        if(item.itemId==R.id.profil_id){

            val intent=Intent(this,profil::class.java)
            startActivity(intent)
        }
        if(item.itemId==R.id.kampanyaOlustur_id){
            val intent=Intent(this,kampanyaOlustur::class.java)
            startActivity(intent)
        }
        if(item.itemId==R.id.mesajlar_id){
            val intent=Intent(this,mesajlar::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupAuthLis() {

        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser

                if (user==null){
                    var intent=Intent(this@MainActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)


                    startActivity(intent)
                    finish()

                }else{

                }

            }

        }

    }
    override fun onStart() {
        super.onStart()
        Log.e("hata","maindesin")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }



}