package com.example.paylasim.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasim.R
import com.example.paylasim.bildirimler.bildirimActivity
import com.example.paylasim.kampanyaolustur.kampanyaOlustur
import com.example.paylasim.login.LoginActivity
import com.example.paylasim.login.signOutFragment
import com.example.paylasim.mesajlar.mesajlar
import com.example.paylasim.models.*
import com.example.paylasim.profil.profil
import com.example.paylasim.util.imageLoader
import com.example.paylasim.util.mainActivityRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
     var tumGonderiler=ArrayList<kullaniciKampanya>()
    var sayfaBasiGonderiler=ArrayList<kullaniciKampanya>()
    var tumPostlar=ArrayList<String>()
    var tumKonumlar=ArrayList<konumlar>()
    val SAYFA_BASI_GONDERI=10
    var sayfaSayisi=1
    var sayfaninSonunaGelindi = false

    private lateinit var recyclerviewadapter:mainActivityRecyclerAdapter



    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener



    @SuppressLint("UseSupportActionBar")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        auth=Firebase.auth
        mref = FirebaseDatabase.getInstance().reference


        locationManager=getSystemService(Context.LOCATION_SERVICE)as LocationManager
        locationListener=object :LocationListener{
            override fun onLocationChanged(p0: Location) {

                //*********** metre cinsinden hesaplama ***************

                /*  val startPoint = Location("locationA")
                  startPoint.setLatitude(p0.latitude)
                  startPoint.setLongitude(p0.longitude)

                  val endPoint = Location("locationA")
                  endPoint.setLatitude(41.001594999999995)
                  endPoint.setLongitude(29.011419999999998)

                  val distance: Double = startPoint.distanceTo(endPoint).toDouble()  */


                var konum=HashMap<String,Any>()
                konum.put("latitude",p0.latitude)
                konum.put("longitude",p0.longitude)





                mref.child("konumlar").child("kullanici_konum").child(FirebaseAuth.getInstance().currentUser!!.uid).child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(konum)

            }

        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
        }


        setupAuthLis()
        initImageLoader()
        tumVerileriGetir()


        refreshMain_id.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {

                tumGonderiler.clear()
                sayfaBasiGonderiler.clear()
                sayfaninSonunaGelindi=false
               verileriGetir()

                refreshMain_id.isRefreshing = false




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

        mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.getValue()!=null){
                    for(ds in p0.children){
                        tumPostlar.add(ds.key!!)
                    }

                    verileriGetir()
                    konumlariGetir()
                }
            }


        })

    }

    private fun konumlariGetir(){

        Log.e("ondata", "tumkonumlarsize:" + tumKonumlar.size)





        mref.child("konumlar").child("kullanici_konum").child(auth.currentUser!!.uid).addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.getValue()!=null){
                        Log.e("ondata", "kullanici" + snapshot)

                        var latitude=snapshot.getValue(kullaniciKonumlari::class.java)!!.latitude
                        var longitude=snapshot.getValue(kullaniciKonumlari::class.java)!!.longitude

                        var eklenecekKonumlar=konumlar()
                        eklenecekKonumlar.kullaniciLatitude=latitude
                        eklenecekKonumlar.kullaniciLongitude=longitude

                        tumKonumlar.add(eklenecekKonumlar)
                        Log.e("konumlar","tumkonumlar"+tumKonumlar)
                        recyclerviewadapter= mainActivityRecyclerAdapter(this@MainActivity,sayfaBasiGonderiler,tumKonumlar)


                        recyclerviewadapter.notifyItemInserted(tumKonumlar.size-1)

                    }


                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {



                        var guncellenecekKonusma = snapshot!!.getValue(konumlar::class.java)
                        guncellenecekKonusma!!.kullaniciLatitude=snapshot.getValue(konumlar::class.java)!!.kullaniciLatitude
                        guncellenecekKonusma.kullaniciLongitude=snapshot.getValue(konumlar::class.java)!!.kullaniciLongitude

                        // myRecyclerView.recycledViewPool.clear()
                        tumKonumlar.clear()
                        recyclerviewadapter.notifyItemRemoved(konumPositionBul(konumlar()))
                        tumKonumlar.add( guncellenecekKonusma!! )
                        recyclerviewadapter.notifyItemInserted(0)


                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }


                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })








    }

    private fun konumPositionBul(kullainiciKonum: konumlar?) : Int{

        for(i in 0..tumKonumlar.size-1){

            var gecici = tumKonumlar.get(i)
            Log.e("gecicikonum","neymis"+gecici)

            if(gecici.kullaniciLatitude!!.equals(kullainiciKonum)&&gecici.kullaniciLongitude!!.equals(kullainiciKonum)){

                return i
            }


        }

        return -1


    }





    private fun verileriGetir() {

        mref=FirebaseDatabase.getInstance().reference






        for(i in 0..tumPostlar.size-1){
            var kullaniciID = tumPostlar.get(i)


            mref.child("users").child("isletmeler").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {


                    Log.e("ondata", "isletmelerondata:" + snapshot)




                    var userID=kullaniciID
                    var kullaniciadi=snapshot.getValue(kullanicilar::class.java)!!.user_name
                    var photoURL=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture
                    var isletmeLati=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.latitude
                    var isletmeLongi=snapshot.getValue(kullanicilar::class.java)!!.user_detail!!.longitude






                    Log.e("isletmelat","isletme lat"+isletmeLongi)













                                mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        Log.e("ondata", "kampanyalarondata:" + snapshot)


                                        if (snapshot!!.hasChildren()){

                                            Log.e("ondata", "kampanyalarondatahascild:" + snapshot.hasChildren())


                                            for (ds in snapshot!!.children){

                                                Log.e("ondata", "kampanyalarfor:" + ds)







                                                var eklenecekUserPost=kullaniciKampanya()

                                                eklenecekUserPost.userID=userID
                                                eklenecekUserPost.userName=kullaniciadi
                                                eklenecekUserPost.userPhotoURL=photoURL
                                                eklenecekUserPost.postID=ds.getValue(kampanya::class.java)!!.post_id
                                                eklenecekUserPost.postURL=ds.getValue(kampanya::class.java)!!.file_url
                                                eklenecekUserPost.postAciklama=ds.getValue(kampanya::class.java)!!.aciklama
                                                eklenecekUserPost.postYuklenmeTarih=ds.getValue(kampanya::class.java)!!.yuklenme_tarih
                                                eklenecekUserPost.geri_sayim=ds.getValue(kampanya::class.java)!!.geri_sayim

                                                eklenecekUserPost.isletmeLatitude=isletmeLati
                                                eklenecekUserPost.isletmeLongitude=isletmeLongi





                                                tumGonderiler.add(eklenecekUserPost)



                                                Log.e("tumgonderiler","size"+tumGonderiler.size)

                                                Log.e("tumgonderiler","içerik"+tumGonderiler)



                                            }
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

                override fun onCancelled(error: DatabaseError) {


                }

            })


        }




    }

    private fun listeyeYeniElemanlariEkle() {

        var yeniGetirilecekElemanlarinAltSiniri = sayfaSayisi * SAYFA_BASI_GONDERI
        var yeniGetirilecekElemanlarinUstSiniri = (sayfaSayisi + 1) * SAYFA_BASI_GONDERI - 1
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

            for (i in 0..SAYFA_BASI_GONDERI - 1) {
                sayfaBasiGonderiler.add(tumGonderiler.get(i))


            }
        } else {
            for (i in 0..tumGonderiler.size - 1) {
                sayfaBasiGonderiler.add(tumGonderiler.get(i))




            }
        }


        val layoutManager= LinearLayoutManager(this@MainActivity)
        recyclerAnaSayfa.layoutManager=layoutManager
        recyclerviewadapter= mainActivityRecyclerAdapter(this@MainActivity,sayfaBasiGonderiler,tumKonumlar)
        recyclerAnaSayfa.adapter=recyclerviewadapter







        recyclerAnaSayfa.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerAnaSayfa!!.layoutManager as LinearLayoutManager

                if (dy > 0 && layoutManager.findLastVisibleItemPosition() == recyclerAnaSayfa!!.adapter!!.itemCount - 1) {

                    if (sayfaninSonunaGelindi == false)
                        listeyeYeniElemanlariEkle()
                }



            }
        })

    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu2,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
       mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
           override fun onDataChange(snapshot: DataSnapshot) {
               if (snapshot!!.getValue()!=null){

                   for (user in snapshot!!.children){
                       var okunanKullanici = user.getValue(kullanicilar::class.java)!!
                       Log.e("murat","okunanKullanici"+okunanKullanici)

                       if(okunanKullanici!!.user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                           Log.e("murat","userid"+okunanKullanici.user_id)


                           var kampanyaMenu=menu!!.findItem(R.id.kampanyaOlustur_id)
                           kampanyaMenu.isVisible=false
                           var profilMenu=menu!!.findItem(R.id.profil_id)
                           profilMenu.isVisible=false
                           var bildirimMenu=menu!!.findItem(R.id.bildirimler_id)
                           bildirimMenu.isVisible=false

                       }


                   }

               }else{
                   var kampanyaMenu=menu!!.findItem(R.id.kampanyaOlustur_id)
                   kampanyaMenu.isVisible=true
                   var profilMenu=menu!!.findItem(R.id.profil_id)
                   profilMenu.isVisible=true
                   var bildirimMenu=menu!!.findItem(R.id.bildirimler_id)
                   bildirimMenu.isVisible=true

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