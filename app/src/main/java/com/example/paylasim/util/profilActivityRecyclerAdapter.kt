package com.example.paylasim.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.models.kullaniciKampanya
import com.example.paylasim.profil.profil
import com.example.paylasim.util.bildirimler.mref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_kampanya_olustur.*
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class profilActivityRecyclerAdapter(var context:Context, var tumKampanyalar:ArrayList<kullaniciKampanya>):RecyclerView.Adapter<profilActivityRecyclerAdapter.MyViewHolder>() {

    init {
        Collections.sort(tumKampanyalar,object :Comparator<kullaniciKampanya>{
            override fun compare(p0: kullaniciKampanya?, p1: kullaniciKampanya?): Int {
                if (p0!!.postYuklenmeTarih!!>p1!!.postYuklenmeTarih!!){
                    return -1
                }else return 1
            }

        })
    }




    class MyViewHolder(itemView: View, profil: Context) : RecyclerView.ViewHolder(itemView) {


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
        var delete=tumLayout.delete


        var myprofilActivity =profil

        @SuppressLint("SetTextI18n")
        fun setData(position: Int, anlikGonderi: kullaniciKampanya) {

            userNameTitle.setText(anlikGonderi.userName)
            imageLoader.setImage(anlikGonderi.userPhotoURL!!, profileImage, null, "")
            Log.e("murat","profilpp"+anlikGonderi.postURL)

            userNameveAciklama.setText(anlikGonderi.userName.toString()+" "+anlikGonderi.postAciklama.toString())
            Picasso.get().load(anlikGonderi.postURL).into(gonderi)

            kampanyaTarihi.setText(TimeAgo.getTimeAgo(anlikGonderi.postYuklenmeTarih!!))

            begeniKontrolu(anlikGonderi)
            yorumlariGoster(position,anlikGonderi)



            yorumYap.setOnClickListener {
                bildirimler.bildirimKaydet(anlikGonderi.userID!!,bildirimler.YORUM_YAPILDI,anlikGonderi.postID!!)



                yorumlarFragmentiniBaslat(anlikGonderi)


            }

            postMenu.visibility=View.GONE

            delete.setOnClickListener {

                var alert = AlertDialog.Builder(myprofilActivity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
                    .setTitle("KAMPANYAYI SİL ")
                    .setMessage("Emin misiniz?")
                    .setPositiveButton("SİL", object : DialogInterface.OnClickListener {

                        override fun onClick(p0: DialogInterface?, p1: Int) {


                            var postID = anlikGonderi.postID

                            mref.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid!!).child(postID!!).addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                   snapshot.ref.removeValue()

                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })

                            mref.child("users").child("isletmeler").child(FirebaseAuth.getInstance().currentUser!!.uid).child("user_detail").addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var oankiGonderiSayisi=snapshot!!.child("post").getValue().toString().toInt()
                                    oankiGonderiSayisi--
                                    mref.child("users").child("isletmeler").child(FirebaseAuth.getInstance().currentUser!!.uid).child("user_detail").child("post").setValue(oankiGonderiSayisi.toString())
                                    val intent=Intent(myprofilActivity,profil::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    myprofilActivity.startActivity(intent)

                                    (myprofilActivity as profil).finish()



                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })




                        }

                    })
                    .setNegativeButton("VAZGEÇ", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                           p0!!. dismiss()
                        }

                    })
                    .create()

                alert.show()

            }


            yorumlariGoster.setOnClickListener {
                yorumlarFragmentiniBaslat(anlikGonderi)
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


        }

        fun yorumlarFragmentiniBaslat(anlikGonderi: kullaniciKampanya) {

            if (anlikGonderi.userID!=FirebaseAuth.getInstance().currentUser!!.uid){

                bildirimler.bildirimKaydet(anlikGonderi.userID!!,bildirimler.YORUM_YAPILDI,anlikGonderi.postID!!)



            }

            EventBus.getDefault()
                .postSticky(EventbusData.YorumYapilacakGonderininIDsiniGonder(anlikGonderi!!.postID))

            (myprofilActivity as profil ).recyclerProfilContainer.visibility = View.INVISIBLE
            (myprofilActivity as profil).profilFragmentContainer.visibility = View.VISIBLE


            var transaction =
                (myprofilActivity as profil).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profilFragmentContainer, yorumlarFragment())
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
        holder.setData(position, tumKampanyalar.get(position))


    }

    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }
}