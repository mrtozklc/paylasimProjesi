package com.example.paylasim.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.models.bildirimModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.recycler_row_bildirim.view.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class bildirimRecyclerAdapter(var mcontext:Context,var tumBildirimler:ArrayList<bildirimModel>):
    RecyclerView.Adapter<bildirimRecyclerAdapter.viewHolder>() {

    init {
        Collections.sort(tumBildirimler, object : Comparator<bildirimModel> {
            override fun compare(o1: bildirimModel?, o2: bildirimModel?): Int {
                if (o1!!.time!! > o2!!.time!!) {
                    return -1
                } else return 1
            }
        })}






    class viewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {

        var tumLayout=itemView as ConstraintLayout
        var gonderiBegenildi=tumLayout.tv_begendi
        var yorumYapildi=tumLayout.tv_begendi
        var yorumBegenildi=tumLayout.tv_begendi
        var begenenPP=tumLayout.begenenpp_id
        var kampanya=tumLayout.begenilenKampanya_id



        fun setdata(anlikBildirim: bildirimModel) {

            if (anlikBildirim.bildirim_tur==1){

                idsiVerilenKullanicininBilgileriBegen(anlikBildirim.user_id, anlikBildirim.gonderi_id,anlikBildirim.time!!)


            }
            else if (anlikBildirim.bildirim_tur==2){


                idsiVerilenKullanicininBilgileriYorum(anlikBildirim.user_id, anlikBildirim.gonderi_id,anlikBildirim.time!!)

            }



        }


        private fun idsiVerilenKullanicininBilgileriYorum(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {

                FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.getValue()!=null){

                            if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE

                            }else{
                                var userName = snapshot!!.child("user_name").getValue().toString()
                                if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                    yorumYapildi.setText(userName + " gönderine yorum yaptı.  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                                if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                    var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                    imageLoader.setImage(takipEdenPicURL, begenenPP, null, "")
                                }

                            }
                        }
                    }




                    override fun onCancelled(error: DatabaseError) {
                    }

                })

                FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot)  {
                        if (snapshot.getValue()!=null){

                            if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE

                            }else{
                                var userName = snapshot!!.child("user_name").getValue().toString()
                                if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                    yorumYapildi.setText(userName + " gönderine yorum yaptı.  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                                if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                    var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                    imageLoader.setImage(takipEdenPicURL, begenenPP, null, "")
                                }

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


                FirebaseDatabase.getInstance().getReference().child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(gonderi_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.getValue()!=null){
                                if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                    begenenPP.visibility=View.GONE
                                    yorumYapildi.visibility=View.GONE
                                    kampanya.visibility=View.GONE}

                                else if (!snapshot!!.child("file_url").getValue().toString().isNullOrEmpty()) {
                                    kampanya.visibility = View.VISIBLE
                                    var begenilenFotoURL = snapshot!!.child("file_url").getValue().toString()
                                    imageLoader.setImage(begenilenFotoURL, kampanya, null, "")
                                } else {
                                    kampanya.visibility = View.INVISIBLE

                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })




        }

        private fun idsiVerilenKullanicininBilgileriBegen(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {




            FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue()!=null){

                        if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            var userName = snapshot!!.child("user_name").getValue().toString()
                            if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                gonderiBegenildi.setText(userName + " Kampanyani Beğendi .  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                            if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                imageLoader.setImage(takipEdenPicURL, begenenPP, null, "")
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot)  {
                    if (snapshot.getValue()!=null){

                        if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            var userName = snapshot!!.child("user_name").getValue().toString()
                            if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                gonderiBegenildi.setText(userName + " Kampanyani Beğendi .  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                            if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                imageLoader.setImage(takipEdenPicURL, begenenPP, null, "")
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


            FirebaseDatabase.getInstance().getReference().child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(gonderi_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.getValue()!=null){
                            if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){  begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE}
                            else


                            if (!snapshot!!.child("file_url").getValue().toString().isNullOrEmpty()) {
                                kampanya.visibility = View.VISIBLE
                                var begenilenFotoURL = snapshot!!.child("file_url").getValue().toString()
                                imageLoader.setImage(begenilenFotoURL, kampanya, null, "")
                            } else {
                                kampanya.visibility = View.INVISIBLE

                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })



    }


}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {

  var view=

      LayoutInflater.from(mcontext).inflate(R.layout.recycler_row_bildirim,parent,false)

  return bildirimRecyclerAdapter.viewHolder(view)
}

override fun onBindViewHolder(holder: viewHolder, position: Int) {

  holder.setdata(tumBildirimler.get(position))

}

override fun getItemCount(): Int {
   return tumBildirimler.size
}
}