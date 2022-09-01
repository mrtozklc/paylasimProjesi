package com.example.paylasim.mesajlar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasim.R
import com.example.paylasim.login.LoginActivity
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.models.mesaj
import com.example.paylasim.util.mesajlarActivityRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*


class chat : AppCompatActivity() {


    lateinit var auth : FirebaseAuth
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    lateinit var sohbetEdilcekKisi:String
    lateinit var mesajGonderenId:String
    lateinit var myRecyclerViewAdapter:mesajlarActivityRecyclerAdapter
    lateinit var myRecyclerview:RecyclerView
    lateinit var eventListener: ChildEventListener
    lateinit var eventListenerRefresh: ChildEventListener

    val gosterilecekMesajSayisi=10
    var sayfaSayisi=1
    var mesajPosition=0
    var refreshMesajPosition=0
    var getirilenMesajId=""
    var zatenListedeOlanMesajID=""
    var tumMesajlar=ArrayList<mesaj>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference
        sohbetEdilcekKisi= intent.getStringExtra("konusulacakKisi").toString()
        sohbetEdilenUserName(sohbetEdilcekKisi)
        mesajGonderenId=auth.currentUser!!.uid




        tvv_mesajGonder.setOnClickListener {

            if (et_mesajEkle.text.toString().equals("")){


            }else{

                var mesajAtan=HashMap<String,Any>()
                mesajAtan.put("mesaj",et_mesajEkle.text.toString())
                mesajAtan.put("gonderilmeZamani",ServerValue.TIMESTAMP)
                mesajAtan.put("type","text")
                mesajAtan.put("goruldu",true)
                mesajAtan.put("user_id",mesajGonderenId)
                var mesajKey = mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).push().key


                mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).child(mesajKey!!).setValue(mesajAtan)

                var mesajAlan=HashMap<String,Any>()
                mesajAlan.put("mesaj",et_mesajEkle.text.toString())
                mesajAlan.put("gonderilmeZamani",ServerValue.TIMESTAMP)
                mesajAlan.put("type","text")
                mesajAlan.put("goruldu",false)
                mesajAlan.put("user_id",mesajGonderenId)
                mref.child("mesajlar").child(sohbetEdilcekKisi).child(mesajGonderenId).child(mesajKey).setValue(mesajAlan)


                var KonusmamesajAtan=HashMap<String,Any>()
                KonusmamesajAtan.put("son_mesaj",et_mesajEkle.text.toString())
                KonusmamesajAtan.put("gonderilmeZamani",ServerValue.TIMESTAMP)
                KonusmamesajAtan.put("goruldu",true)

                mref.child("konusmalar").child(mesajGonderenId).child(sohbetEdilcekKisi).setValue(KonusmamesajAtan)

                var KonusmamesajAlan=HashMap<String,Any>()
                KonusmamesajAlan.put("son_mesaj",et_mesajEkle.text.toString())
                KonusmamesajAlan.put("gonderilmeZamani",ServerValue.TIMESTAMP)
                KonusmamesajAlan.put("goruldu",false)
                mref.child("konusmalar").child(sohbetEdilcekKisi).child(mesajGonderenId).setValue(KonusmamesajAlan)

                et_mesajEkle.setText("")


            }




        }

        refresh_id.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {

                mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot!!.childrenCount.toInt() != tumMesajlar.size){
                            refreshMesajPosition=0



                            refreshMesajlar()

                        }else{

                            refresh_id.isRefreshing = false
                            refresh_id.isEnabled = false

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


                refresh_id.isRefreshing=false
            }

        })

        setupAuthLis()
    }

    private fun mesajlariGetir() {

        tumMesajlar.clear()



       eventListener= mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var okunanMesaj=snapshot.getValue(com.example.paylasim.models.mesaj::class.java)
                tumMesajlar.add(okunanMesaj!!)

                if (mesajPosition==0){

                    getirilenMesajId= snapshot!!.key!!
                    zatenListedeOlanMesajID=snapshot!!.key!!

                }
                mesajPosition++




                myRecyclerViewAdapter.notifyItemChanged(tumMesajlar.size-1)
                myRecyclerview.scrollToPosition(tumMesajlar.size-1)


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun refreshMesajlar(){

        eventListenerRefresh=mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).orderByKey().endAt(getirilenMesajId).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                var okunanMesaj=snapshot.getValue(com.example.paylasim.models.mesaj::class.java)

                if(!zatenListedeOlanMesajID.equals(snapshot!!.key)){

                    tumMesajlar.add(refreshMesajPosition++,okunanMesaj!!)
                    myRecyclerViewAdapter.notifyItemInserted(refreshMesajPosition-1)

                }else {

                    zatenListedeOlanMesajID=getirilenMesajId

                }

                if(refreshMesajPosition==1){

                    getirilenMesajId=snapshot!!.key!!


                }








                refresh_id.isRefreshing = false
                myRecyclerViewAdapter.notifyDataSetChanged()
                myRecyclerview.scrollToPosition(0)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun mesajlarRecyclerview() {
        val myLinearLayoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        myLinearLayoutManager.stackFromEnd=true
        myRecyclerview=sohbetRecycler
        myRecyclerViewAdapter=mesajlarActivityRecyclerAdapter(tumMesajlar,this)

        myRecyclerview.layoutManager=myLinearLayoutManager
        myRecyclerview.adapter=myRecyclerViewAdapter

        mesajlariGetir()

    }

    private fun sohbetEdilenUserName(sohbetEdilcekKisi: String) {

        mref.child("users").child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot .getValue()!=null){

                    var bulunanKullanici=snapshot!!.getValue(kullanicilar::class.java)!!.user_name
                    tv_mesajlasılanUserName.setText(bulunanKullanici)

                    mesajlarRecyclerview()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun setupAuthLis() {


        mauthLis=object : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user= FirebaseAuth.getInstance().currentUser

                if (user==null){
                    var intent=
                        Intent(this@chat, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
        Log.e("hata","chattesin")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        if (mauthLis!=null){
            auth.removeAuthStateListener(mauthLis)

        }
    }
}