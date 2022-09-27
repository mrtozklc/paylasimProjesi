package com.example.paylasim.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.home.MainActivity
import com.example.paylasim.kampanyaolustur.kampanyaOlustur
import com.example.paylasim.login.LoginActivity
import com.example.paylasim.mesajlar.chat
import com.example.paylasim.models.kampanya
import com.example.paylasim.models.konumlar
import com.example.paylasim.models.kullaniciKampanya
import com.example.paylasim.profil.profil
import com.example.paylasim.profil.profilAyarlarActivity
import com.example.paylasim.profil.userProfil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class mainActivityRecyclerAdapter(var context:Context,var tumKampanyalar:ArrayList<kullaniciKampanya>,var tumKonumlar:ArrayList<konumlar>):RecyclerView.Adapter<mainActivityRecyclerAdapter.MyViewHolder>() {










    class MyViewHolder(itemView: View, mainActivity: Context) : RecyclerView.ViewHolder(itemView) {




        var tumLayout = itemView as ConstraintLayout
        var profileImage = tumLayout.profil_image
        var userNameTitle = tumLayout.kullaniciAdiTepe
        var gonderi = tumLayout.kampanyaPhoto
        var userNameveAciklama = tumLayout.textView21
        var kampanyaTarihi = tumLayout.kampanyaTarihi_id
        var yorumYap = tumLayout.img_yorum
        var gonderiBegen = tumLayout.img_begen
        var begenmeSayisi=tumLayout.begenmeSayisi
        var yorumlariGoster=tumLayout.tv_yorumGoster
        var postMenu=tumLayout.post_mesaj
        var myMainActivity = mainActivity
        var geriSayim=tumLayout.geri_sayim_id
        var mesafe=tumLayout.tw_mesafe





        @SuppressLint("SetTextI18n")
        fun setData(position: Int, anlikGonderi: kullaniciKampanya,anlikkonumlar: konumlar) {

            userNameTitle.setText(anlikGonderi.userName)

            if (anlikGonderi.isletmeLatitude!=0.0 && anlikGonderi.isletmeLongitude!=0.0){

                val startPoint = Location("locationA")
                startPoint.setLatitude(anlikGonderi.isletmeLatitude!!)
                startPoint.setLongitude(anlikGonderi.isletmeLongitude!!)

                val endPoint = Location("locationA")
                endPoint.setLatitude(anlikkonumlar.kullaniciLatitude!!)
                endPoint.setLongitude(anlikkonumlar.kullaniciLongitude!!)

                val distance: Int = startPoint.distanceTo(endPoint).toDouble().toInt()

                mesafe.setText(distance.toString()+"   metre")

            }





            imageLoader.setImage(anlikGonderi.userPhotoURL!!,profileImage,null,"")



            // Picasso.get().load(anlikGonderi.userPhotoURL).fit().centerCrop().into(profileImage)



            userNameveAciklama.setText(anlikGonderi.userName.toString()+" "+anlikGonderi.postAciklama.toString())

            Picasso.get().load(anlikGonderi.postURL).fit().centerCrop().into(gonderi)


           kampanyaTarihi.setText(TimeAgo.getTimeAgo(anlikGonderi.postYuklenmeTarih!!))


            if (anlikGonderi.geri_sayim=="1 saat"){

                var time = anlikGonderi.postYuklenmeTarih
                if (time != null) {
                    if (time < 1000000000000L) {
                        // if timestamp given in seconds, convert to millis
                        time *= 1000
                    }
                }
                val now = System.currentTimeMillis()
                if (time != null) {
                    if (time > now || time <= 0) {


                    }else{

                        val difff =  now - time!!
                        if (difff>=1800000){
                            geriSayim.setText("kampanya süresi doldu!")


                        }


                        object : CountDownTimer(1800000-difff, 1000) {

                            override fun onTick(millisUntilFinished: Long) {
                                var diff = millisUntilFinished
                                val secondsInMilli: Long = 1000
                                val minutesInMilli = secondsInMilli * 60
                                val hoursInMilli = minutesInMilli * 60


                                val elapsedHours = diff / hoursInMilli
                                diff %= hoursInMilli

                                val elapsedMinutes = diff / minutesInMilli
                                diff %= minutesInMilli

                                val elapsedSeconds = diff / secondsInMilli



                                geriSayim.setText("$elapsedHours s $elapsedMinutes d $elapsedSeconds ")

                            }

                            override fun onFinish() {
                                geriSayim.setText("kampanya süresi doldu!")
                            }

                        }.start()


                    }
                }


            }
            else if (anlikGonderi.geri_sayim=="2 saat"){


                object : CountDownTimer(3600000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        var diff = millisUntilFinished
                        val secondsInMilli: Long = 1000
                        val minutesInMilli = secondsInMilli * 60
                        val hoursInMilli = minutesInMilli * 60


                        val elapsedHours = diff / hoursInMilli
                        diff %= hoursInMilli

                        val elapsedMinutes = diff / minutesInMilli
                        diff %= minutesInMilli

                        val elapsedSeconds = diff / secondsInMilli

                        geriSayim.setText("$elapsedHours s $elapsedMinutes d $elapsedSeconds ")

                    }

                    override fun onFinish() {
                        geriSayim.setText("kampanya süresi doldu!")
                    }

                }.start()

            }




            userNameTitle.setOnClickListener {


                if (anlikGonderi.userID!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){

                    val intent=Intent(myMainActivity,profil::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    myMainActivity.startActivity(intent)

                }else{


                    val intent=Intent(myMainActivity,userProfil::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("secilenUserId",anlikGonderi.userID!!)
                    Log.e("murat","secilenuser"+anlikGonderi.userID)
                    myMainActivity.startActivity(intent)


                }




            }

            begeniKontrolu(anlikGonderi)
            yorumlariGoster(position,anlikGonderi)


            yorumYap.setOnClickListener {



                yorumlarFragmentiniBaslat(anlikGonderi)






            }

            var mauth=FirebaseAuth.getInstance().currentUser!!.uid

            if (anlikGonderi.userID.equals(mauth)){
                postMenu.visibility=View.GONE
            }

            gonderiBegen.setOnClickListener {

                var ref = FirebaseDatabase.getInstance().reference
                var currentID = FirebaseAuth.getInstance().currentUser!!.uid

                ref.child("begeniler").child(anlikGonderi.postID!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.hasChild(currentID)) {

                                ref.child("begeniler").child(anlikGonderi.postID!!).child(currentID)
                                    .removeValue()




                                if (anlikGonderi.userID!=FirebaseAuth.getInstance().currentUser!!.uid){
                                    bildirimler.bildirimKaydet(anlikGonderi.userID!!,bildirimler.KAMPANYA_BEGENILDI_GERI,anlikGonderi.postID!!)


                                }

                                gonderiBegen.setImageResource(R.drawable.ic_launcher_like_foreground)

                            } else {
                                ref.child("begeniler").child(anlikGonderi.postID!!).child(currentID)
                                    .setValue(currentID)

                                if (anlikGonderi.userID!=FirebaseAuth.getInstance().currentUser!!.uid){
                                    bildirimler.bildirimKaydet(anlikGonderi.userID!!,bildirimler.KAMPANYA_BEGENILDI,anlikGonderi.postID!!)
                                }
                                gonderiBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)
                                begenmeSayisi.visibility=View.VISIBLE
                                begenmeSayisi.setText(""+snapshot!!.childrenCount!!.toString()+" beğeni")



                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

            }

            yorumlariGoster.setOnClickListener {
                yorumlarFragmentiniBaslat(anlikGonderi)
            }





            postMenu.setOnClickListener {
                var mref=FirebaseDatabase.getInstance().reference
                var mauth=FirebaseAuth.getInstance().currentUser!!.uid


                mref.child("kampanya").child(anlikGonderi.userID!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot!!.getValue()!=null){
                            if (!anlikGonderi.userID.equals(mauth)){
                                var intent=Intent(myMainActivity, chat::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra("konusulacakKisi",anlikGonderi.userID)


                                myMainActivity.startActivity(intent)

                            }else{
                                var intent=Intent(myMainActivity, profil::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                myMainActivity.startActivity(intent)

                            }




                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })




            }






        }

        fun yorumlarFragmentiniBaslat(anlikGonderi: kullaniciKampanya) {

            if (anlikGonderi.userID!=FirebaseAuth.getInstance().currentUser!!.uid){

                bildirimler.bildirimKaydet(anlikGonderi.userID!!,bildirimler.YORUM_YAPILDI,anlikGonderi.postID!!)



            }



            EventBus.getDefault()
                .postSticky(EventbusData.YorumYapilacakGonderininIDsiniGonder(anlikGonderi!!.postID))

            (myMainActivity as MainActivity).recyclerMainContainer.visibility = View.INVISIBLE
            (myMainActivity as MainActivity).mainFragmentContainer.visibility = View.VISIBLE


            var transaction =
                (myMainActivity as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFragmentContainer, yorumlarFragment())
            transaction.addToBackStack("yorumlarFragmentEklendi")
            transaction.commit()

        }

        private fun yorumlariGoster(position: Int, anlikGonderi: kullaniciKampanya) {
            var mref=FirebaseDatabase.getInstance().reference
            mref.child("yorumlar").child(anlikGonderi.postID!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var yorumSayisi=0

                    for (ds in snapshot!!.children){
                        if (!ds!!.key.toString().equals(anlikGonderi.postID)){
                            yorumSayisi++
                        }

                    }


                    if (yorumSayisi>=1){
                        yorumlariGoster.visibility=View.VISIBLE
                        yorumlariGoster.setText(yorumSayisi.toString()+"  yorum")


                    }else{
                        yorumlariGoster.visibility=View.GONE
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

        fun begeniKontrolu(anlikGonderi: kullaniciKampanya) {
            var mRef = FirebaseDatabase.getInstance().reference
            var userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("begeniler").child(anlikGonderi.postID!!).addValueEventListener(object : ValueEventListener {


                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot!!.getValue()!=null){
                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.setText(""+snapshot!!.childrenCount!!.toString()+" beğeni")

                    }else {
                        begenmeSayisi.visibility=View.GONE
                    }

                    if (snapshot!!.hasChild(userID)) {
                        gonderiBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)
                    } else {
                        gonderiBegen.setImageResource(R.drawable.ic_launcher_like_foreground)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }


            })




        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false)

        return MyViewHolder(viewHolder,context)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.setData(position, tumKampanyalar.get(position),tumKonumlar.get(position))
            Log.e("tumkonumlar","getposition:"+tumKonumlar.get(position))





    }

    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }
}