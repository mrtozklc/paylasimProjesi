package com.example.paylasim.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.SupportMenuInflater
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasim.R
import com.example.paylasim.bildirimler.bildirimActivity
import com.example.paylasim.kampanyaolustur.kampanyaOlustur
import com.example.paylasim.kampanyaolustur.sabitKampanyaOlustur
import com.example.paylasim.login.LoginActivity
import com.example.paylasim.login.signOutFragment
import com.example.paylasim.mesajlar.mesajlar
import com.example.paylasim.models.*
import com.example.paylasim.profil.profil
import com.example.paylasim.util.imageLoader
import com.example.paylasim.util.mainActivityRecyclerAdapter
import com.example.paylasim.util.sabitKampanyaRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nostra13.universalimageloader.core.ImageLoader
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_mesajlar.*
import kotlinx.android.synthetic.main.recycler_row_sabit_kampanyalar.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    var tumGonderiler=ArrayList<kullaniciKampanya>()
    var tumSabitKampanyalar=ArrayList<sabitKampanya>()
    var sayfaBasiGonderiler=ArrayList<kullaniciKampanya>()
    var tumPostlar=ArrayList<String>()
    val SAYFA_BASI_GONDERI=10
    var sayfaSayisi=1
    var sayfaninSonunaGelindi = false



    private lateinit var recyclerviewadapter:mainActivityRecyclerAdapter
    private  lateinit var  recyclerSabitAdapter:sabitKampanyaRecyclerAdapter



    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener



    @SuppressLint("UseSupportActionBar")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth=Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        progressBar8.visibility=View.VISIBLE
        refreshMain_id.visibility=View.GONE
        recyclerAnaSayfa.visibility=View.GONE


        setupAuthLis()
        tumVerileriGetir()
        tumSabitKampanyalariGetir()



        initImageLoader()






        locationManager=getSystemService(Context.LOCATION_SERVICE)as LocationManager
        locationListener=object :LocationListener{
            override fun onLocationChanged(p0: Location) {


                var konum=HashMap<String,Any>()
                konum.put("latitude",p0.latitude)
                konum.put("longitude",p0.longitude)
                konum.put("konumkullaniciId",auth.currentUser?.uid.toString())

                mref.child("konumlar").child("kullanici_konum").child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(konum)

            }

            override fun onProviderDisabled(provider: String) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
        }





        refreshMain_id.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {


                tumGonderiler.clear()
                sayfaBasiGonderiler.clear()
                sayfaninSonunaGelindi=false

                recyclerviewadapter.notifyDataSetChanged()
               verileriGetir()
                refreshMain_id.isRefreshing = false

            }


        })



    }

    private fun tumSabitKampanyalariGetir() {


        mref.child("sabitKampanyalar").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.getValue()!=null) {

                    if (snapshot!!.hasChildren()) {


                        for (ds in snapshot!!.children) {

                            for (ds in ds.children){
                                var eklenecekSabitKampanya = sabitKampanya()

                                eklenecekSabitKampanya.post_id=ds.getValue(sabitKampanya::class.java)!!.post_id
                                eklenecekSabitKampanya.file_url=ds.getValue(sabitKampanya::class.java)!!.file_url
                                eklenecekSabitKampanya.aciklama=ds.getValue(sabitKampanya::class.java)!!.aciklama
                                eklenecekSabitKampanya.user_id=ds.getValue(sabitKampanya::class.java)!!.user_id
                                eklenecekSabitKampanya.yuklenme_tarih=ds.getValue(sabitKampanya::class.java)!!.yuklenme_tarih

                                tumSabitKampanyalar.add(eklenecekSabitKampanya)

                                Log.e("eklenecek","kampanyalarsabit:"+tumSabitKampanyalar)

                            }



                            val adapter = sabitKampanyaRecyclerAdapter(this@MainActivity,tumSabitKampanyalar)

                            val sliderView = findViewById<SliderView>(R.id.slider)


                            sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR


                            sliderView.setSliderAdapter(adapter)

                            sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)

                            sliderView.scrollTimeInSec = 3
                            sliderView.setIndicatorSelectedColor(Color.WHITE);
                            sliderView.setIndicatorUnselectedColor(Color.GRAY);


                            sliderView.isAutoCycle = true

                            sliderView.startAutoCycle()














                        }



                    }
                }





            }





            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun onBackPressed() {
       recyclerMainContainer.visibility= View.VISIBLE
      mainFragmentContainer.visibility= View.GONE
        super.onBackPressed()


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){

            if (grantResults.isNotEmpty()){
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

                }

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }





    private fun tumVerileriGetir(){
        tumGonderiler.clear()
        sayfaBasiGonderiler.clear()


        mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.
                    getValue()!=null){
                    for(ds in p0.children){
                        tumPostlar.add(ds.key!!)
                    }


                    verileriGetir()


                }
            }


        })


        if (tumGonderiler.size==0){

            mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.getValue()==null){
                       progressBar8.visibility=View.VISIBLE
                       kampanyaYok.visibility=View.VISIBLE
                       progressBar8.visibility=View.GONE
                       refreshMain_id.visibility=View.VISIBLE

                       recyclerviewadapter= mainActivityRecyclerAdapter(this@MainActivity,sayfaBasiGonderiler)
                       refreshMain_id.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
                           override fun onRefresh() {

                               tumGonderiler.clear()
                               sayfaBasiGonderiler.clear()

                               tumVerileriGetir()


                               refreshMain_id.isRefreshing = false



                           }


                       })


                   }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



        }




    }


    /*private fun konumlariGetir(){

        Log.e("ondata", "tumkonumlarsize:" + tumKullaniciKonumlari.size)


        mref.child("konumlar").child("kullanici_konum").child(auth.currentUser!!.uid)
            .child(auth.currentUser!!.uid).addValueEventListener(object:ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {

                    tumKullaniciKonumlari.clear()
                    if (snapshot.exists()){
                        for (i in snapshot.children){

                            Log.e("ondata", "id = ${auth.currentUser?.uid}  kullanici:::: $i ::::: kullanici1" + snapshot)

                            var latitude=snapshot.getValue(kullaniciKonumlari::class.java)!!.latitude
                            var longitude=snapshot.getValue(kullaniciKonumlari::class.java)!!.longitude

                            var eklenecekKonumlar=kullaniciKonumlari()
                            eklenecekKonumlar.latitude=latitude
                            eklenecekKonumlar.longitude=longitude


                            tumKullaniciKonumlari.add(eklenecekKonumlar)

                        }
                    }
                    else{
                        Log.e("TAG","HATA")
                    }


                    val layoutManager= LinearLayoutManager(this@MainActivity)
                    recyclerAnaSayfa.layoutManager=layoutManager
                    recyclerviewadapter= mainActivityRecyclerAdapter(this@MainActivity,sayfaBasiGonderiler,tumKullaniciKonumlari)
                    recyclerAnaSayfa.adapter=recyclerviewadapter
                    recyclerviewadapter.notifyDataSetChanged()

                    Log.e("konumlar","tumkonumlar"+tumKullaniciKonumlari)


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })




    }*/


    private fun verileriGetir() {




        mref=FirebaseDatabase.getInstance().reference



        for(i in 0..tumPostlar.size-1){

              var kullaniciID = tumPostlar.get(i)


              Log.e("kullaniciID", "kullaniciID:" +tumPostlar.get(i))

              mref.child("users").child("isletmeler").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                  override fun onDataChange(snapshot: DataSnapshot) {

                      var userID=snapshot.getValue(kullanicilar::class.java)!!.user_id
                      var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                      var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture
                      var isletmeLati=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.latitude
                      var isletmeLongi=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.longitude




                      mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                          override fun onDataChange(snapshot: DataSnapshot) {

                              if (snapshot.getValue()!=null) {

                                  if (snapshot!!.hasChildren()) {


                                      for (ds in snapshot!!.children) {

                                          var eklenecekUserPost = kullaniciKampanya()

                                          eklenecekUserPost.userID = userID
                                          eklenecekUserPost.userName = kullaniciadi
                                          eklenecekUserPost.userPhotoURL = photoURL
                                          eklenecekUserPost.postID = ds.getValue(kampanya::class.java)!!.post_id
                                          eklenecekUserPost.postURL = ds.getValue(kampanya::class.java)!!.file_url
                                          eklenecekUserPost.postAciklama = ds.getValue(kampanya::class.java)!!.aciklama
                                          eklenecekUserPost.postYuklenmeTarih = ds.getValue(kampanya::class.java)!!.yuklenme_tarih
                                          eklenecekUserPost.geri_sayim = ds.getValue(kampanya::class.java)!!.geri_sayim
                                          eklenecekUserPost.isletmeLatitude = isletmeLati
                                          eklenecekUserPost.isletmeLongitude = isletmeLongi

                                          tumGonderiler.add(eklenecekUserPost)




                                      }



                                  }
                              }else
                              {
                              }


                              if(i>=tumPostlar.size-1){

                                  if (tumPostlar.size>0){
                                      setUpRecyclerview()
                                  }

                              }



                          }





                          override fun onCancelled(error: DatabaseError) {
                          }

                      })




                  }

                  override fun onCancelled(error: DatabaseError) {}

              })


          }








    }

    private fun listeyeYeniElemanlariEkle() {

        var yeniGetirilecekElemanlarinAltSiniri = sayfaSayisi * SAYFA_BASI_GONDERI
        var yeniGetirilecekElemanlarinUstSiniri = (sayfaSayisi +1) * SAYFA_BASI_GONDERI - 1
        for (i in yeniGetirilecekElemanlarinAltSiniri..yeniGetirilecekElemanlarinUstSiniri) {
            if (sayfaBasiGonderiler.size <= tumGonderiler.size - 1) {
                sayfaBasiGonderiler.add(tumGonderiler.get(i))
                recyclerAnaSayfa!!.adapter!!.notifyDataSetChanged()
            } else {
                sayfaninSonunaGelindi = true
                sayfaSayisi = 0
                break
            }

        }
        Log.e("XXX", "" + yeniGetirilecekElemanlarinAltSiniri + " dan " + yeniGetirilecekElemanlarinUstSiniri + " kadar eleman eklendi")
        sayfaSayisi++


    }

    private fun setUpRecyclerview(){

        Collections.sort(tumGonderiler,object :Comparator<kullaniciKampanya>{
            override fun compare(p0: kullaniciKampanya?, p1: kullaniciKampanya?): Int {
                if (p0!!.postYuklenmeTarih!!>p1!!.postYuklenmeTarih!!){
                    return -1
                }else return 1
            }

        })

        if (tumGonderiler.size >= SAYFA_BASI_GONDERI) {


            for (i in 0..SAYFA_BASI_GONDERI-1) {
                sayfaBasiGonderiler.add(tumGonderiler.get(i))




            }
        } else {
            for (i in 0..tumGonderiler.size-1) {
                sayfaBasiGonderiler.add(tumGonderiler.get(i))








            }
        }

        val layoutManager= LinearLayoutManager(this@MainActivity)
        recyclerAnaSayfa.layoutManager=layoutManager
        recyclerviewadapter= mainActivityRecyclerAdapter(this@MainActivity,sayfaBasiGonderiler)
        recyclerAnaSayfa.adapter=recyclerviewadapter



          progressBar8.visibility=View.GONE
          kampanyaYok.visibility=View.GONE
          refreshMain_id.visibility=View.VISIBLE
          recyclerAnaSayfa.visibility=View.VISIBLE
          recyclerMainContainer.visibility=View.VISIBLE















        recyclerAnaSayfa.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)


                if (dy > 0 && layoutManager.findLastVisibleItemPosition() == recyclerAnaSayfa!!.adapter!!.itemCount - 1) {

                    if (sayfaninSonunaGelindi == false)
                        listeyeYeniElemanlariEkle()
                }


            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        menuInflater.inflate(R.menu.menu2,menu)


        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {


       mref.child("users").child("kullanicilar").addValueEventListener(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot!!.getValue()!=null){

                   for (user in snapshot!!.children){
                       var okunanKullanici = user.getValue(kullanicilar::class.java)!!

                       if (okunanKullanici!!.user_name.equals("adminn")){

                           var kampanyaMenu=menu!!.findItem(R.id.kampanyaOlustur_id)
                           kampanyaMenu.isVisible=true
                           var profilMenu=menu!!.findItem(R.id.profil_id)
                           profilMenu.isVisible=true
                           var bildirimMenu=menu!!.findItem(R.id.bildirimler_id)
                           bildirimMenu.isVisible=true
                           var sabitKampanya=menu!!.findItem(R.id.sabitkampanyaOlustur_id)
                           sabitKampanya.isVisible=true


                       }

                      else if(okunanKullanici!!.user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                           Log.e("murat","userid"+okunanKullanici.user_id)


                           var kampanyaMenu=menu!!.findItem(R.id.kampanyaOlustur_id)
                           kampanyaMenu.isVisible=false
                           var profilMenu=menu!!.findItem(R.id.profil_id)
                           profilMenu.isVisible=false
                           var bildirimMenu=menu!!.findItem(R.id.bildirimler_id)
                           bildirimMenu.isVisible=false
                           var sabitKampanya=menu!!.findItem(R.id.sabitkampanyaOlustur_id)
                           sabitKampanya.isVisible=false

                       }
                       else{
                           var kampanyaMenu=menu!!.findItem(R.id.kampanyaOlustur_id)
                           kampanyaMenu.isVisible=true
                           var profilMenu=menu!!.findItem(R.id.profil_id)
                           profilMenu.isVisible=true
                           var bildirimMenu=menu!!.findItem(R.id.bildirimler_id)
                           bildirimMenu.isVisible=true
                           var sabitKampanya=menu!!.findItem(R.id.sabitkampanyaOlustur_id)
                           sabitKampanya.isVisible=false

                       }


                   }

               }
           }

           override fun onCancelled(error: DatabaseError) {

           }

       })
        return super.onPrepareOptionsMenu(menu)
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

        if(item.itemId==R.id.sabitkampanyaOlustur_id){
            val intent=Intent(this,sabitKampanyaOlustur::class.java)
            startActivity(intent)
        }
        if(item.itemId==R.id.mesajlar_id){
            val intent=Intent(this,mesajlar::class.java)
            startActivity(intent)
        }
        if(item.itemId==R.id.bildirimler_id){
            val intent=Intent(this,bildirimActivity::class.java)
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

    private fun initImageLoader(){
        var universalImageLoaderr= imageLoader(this)
        ImageLoader.getInstance().init(universalImageLoaderr.config)

    }



}