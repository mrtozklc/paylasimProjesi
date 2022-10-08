package com.example.paylasim.mesajlar

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.bildirimler.bildirimActivity
import com.example.paylasim.kampanyaolustur.kampanyaOlustur
import com.example.paylasim.login.LoginActivity
import com.example.paylasim.login.signOutFragment
import com.example.paylasim.models.konusmalar
import com.example.paylasim.models.mesaj
import com.example.paylasim.profil.profil
import com.example.paylasim.util.bildirimler
import com.example.paylasim.util.konusmalarRecyclerAdapter
import com.example.paylasim.util.mainActivityRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_bildirim.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mesajlar.*
import kotlinx.android.synthetic.main.activity_mesajlar.imageView_back

class mesajlar : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    var tumKonusmalar=ArrayList<konusmalar>()
    lateinit var mrecyclerview:RecyclerView
    lateinit var mlinearlayoutmanager:LinearLayoutManager
    lateinit var madapter:konusmalarRecyclerAdapter
    var listenerAtandiMi=false


    companion object {
        var activitiyAcikMi=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesajlar)

        auth=  FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference




        progressBarMesajlar.visibility= View.VISIBLE
        recyclerMesajlar.visibility=View.INVISIBLE
        setAdapter()
        setupAuthLis()

        imageView_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAdapter() {

        mrecyclerview=this@mesajlar.recyclerMesajlar
        mlinearlayoutmanager= LinearLayoutManager(this@mesajlar,LinearLayoutManager.VERTICAL,false)
        madapter= konusmalarRecyclerAdapter(tumKonusmalar,this@mesajlar)

        mrecyclerview.layoutManager=mlinearlayoutmanager
        mrecyclerview.adapter=madapter



        konusmalariGetir()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if (tumKonusmalar.size!=0){
            menuInflater.inflate(R.menu.menu,menu)

        }


        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        if (item.itemId== R.id.mesajlariSil_id){


            var alert = androidx.appcompat.app.AlertDialog.Builder(this , androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
                .setTitle("Tüm mesajlari sil? ")
                .setPositiveButton("Sil", object : DialogInterface.OnClickListener {

                    override fun onClick(p0: DialogInterface?, p1: Int) {

                        mref.child("mesajlar").child(FirebaseAuth.getInstance().currentUser!!.uid!!).addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()

                                madapter.notifyItemRemoved(tumKonusmalar.size)



                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })

                        mref.child("konusmalar").child(FirebaseAuth.getInstance().currentUser!!.uid!!).addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()
                                madapter.notifyItemRemoved(tumKonusmalar.size)



                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })





                    }

                })
                .setNegativeButton("Vazgeç", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!. dismiss()
                    }

                })
                .create()

            alert.show()

        }

        return super.onOptionsItemSelected(item)
    }

    private fun konusmalariGetir() {

        if (listenerAtandiMi==false){
            listenerAtandiMi=true
            mref.child("konusmalar").child(auth.currentUser!!.uid).orderByChild("gonderilmeZamani").addChildEventListener(mListener)

            object : CountDownTimer(1000,1000){
                override fun onFinish() {

                    progressBarMesajlar.visibility= View.GONE
                    recyclerMesajlar.visibility=View.VISIBLE

                }

                override fun onTick(p0: Long) {

                }

            }.start()


            if (tumKonusmalar.size==0){
                mref.child("konusmalar").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.getValue()==null){
                            mesaj_yok.visibility=View.VISIBLE
                            recyclerMesajlar.visibility=View.GONE
                        }else{
                            mesaj_yok.visibility=View.GONE
                            recyclerMesajlar.visibility=View.VISIBLE

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            }

        }



    }
    private var mListener=object :ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            mesaj_yok.visibility=View.GONE
            recyclerMesajlar.visibility=View.VISIBLE
            var eklenecekKonusma=snapshot.getValue(konusmalar::class.java)
            eklenecekKonusma!!.user_id=snapshot.key
            tumKonusmalar.add(0,eklenecekKonusma!!)
            madapter.notifyItemInserted(0)

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            var kontrol =konusmaPositionBul(snapshot!!.key.toString())
            if(kontrol != -1){

                var guncellenecekKonusma = snapshot!!.getValue(konusmalar::class.java)
                guncellenecekKonusma!!.user_id=snapshot!!.key


                tumKonusmalar.removeAt(kontrol)
                madapter.notifyItemRemoved(kontrol)
                tumKonusmalar.add(0,guncellenecekKonusma)
                madapter.notifyItemInserted(0)


            }

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            var kontrol =konusmaPositionBul(snapshot!!.key.toString())
            if(kontrol != -1){

                var guncellenecekKonusma = snapshot!!.getValue(konusmalar::class.java)
                guncellenecekKonusma!!.user_id=snapshot!!.key


                tumKonusmalar.removeAt(kontrol)
                madapter.notifyItemRemoved(kontrol)




            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }


    private fun konusmaPositionBul(userID : String) : Int{


        for(i in 0..tumKonusmalar.size-1){
            var gecici = tumKonusmalar.get(i)

            if(gecici.user_id.equals(userID)){
                return i
            }
        }

        return -1


    }

    private fun setupAuthLis() {

        mauthLis=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user= FirebaseAuth.getInstance().currentUser

                if (user==null){
                    var intent=
                        Intent(this@mesajlar, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)


                    startActivity(intent)
                    finish()

                }else{

                }

            }

        }

    }

    override fun onPause() {
        activitiyAcikMi=false
        super.onPause()

        tumKonusmalar.clear()
        if(listenerAtandiMi==true){
            listenerAtandiMi=false
            mref.child("konusmalar").child(FirebaseAuth.getInstance().currentUser!!.uid).removeEventListener(mListener)
        }


    }
    override fun onResume() {
        super.onResume()
        activitiyAcikMi=true

        tumKonusmalar.clear()


        if(listenerAtandiMi==false){
            listenerAtandiMi=true
            madapter.notifyDataSetChanged()
            mref.child("konusmalar").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{


                override fun onDataChange(snapshot: DataSnapshot) {
                    progressBarMesajlar.visibility=View.VISIBLE
                   recyclerMesajlar.visibility=View.GONE
                    mref.child("konusmalar").child(FirebaseAuth.getInstance().currentUser!!.uid).orderByChild("gonderilmeZamani").addChildEventListener(mListener)
                    object : CountDownTimer(1000,1000){
                        override fun onFinish() {
                           progressBarMesajlar.visibility=View.GONE
                           recyclerMesajlar.visibility=View.VISIBLE
                        }

                        override fun onTick(p0: Long) {

                        }

                    }.start()                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }
    }


    override fun onStart() {
        super.onStart()

       activitiyAcikMi=true
    }

    override fun onStop() {
        activitiyAcikMi=false
        super.onStop()

    }
}