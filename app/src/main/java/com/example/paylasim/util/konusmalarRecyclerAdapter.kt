package com.example.paylasim.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.mesajlar.chat
import com.example.paylasim.mesajlar.mesajlar
import com.example.paylasim.models.konusmalar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.recycler_row_konusmalar.view.*


class konusmalarRecyclerAdapter(var tumKonusmalar:ArrayList<konusmalar>,var myContext: Context):RecyclerView.Adapter<konusmalarRecyclerAdapter.myViewHolder>() {


    class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mref= FirebaseDatabase.getInstance().reference


        fun setdata(oankiKonusmalar: konusmalar) {

           var sonAtilanmesajText=oankiKonusmalar.son_mesaj.toString()


            if(!sonAtilanmesajText.isNullOrEmpty()){
                sonAtilanmesajText=sonAtilanmesajText.replace("\n"," ")
                sonAtilanmesajText=sonAtilanmesajText.trim()

                if(sonAtilanmesajText.length>25){
                    sonAtilanmesaj.text=sonAtilanmesajText.substring(0,25)+"..."
                }else{
                    sonAtilanmesaj.text=sonAtilanmesajText
                }
            }else{
                sonAtilanmesajText=""
                sonAtilanmesaj.text=sonAtilanmesajText
            }

            gonderilmeZamani.text=TimeAgo.getTimeAgoForComments(oankiKonusmalar.gonderilmeZamani!!.toLong())

            if(oankiKonusmalar.goruldu==false){


                okunduBilgisi.visibility=View.VISIBLE
                userName.setTypeface(null, Typeface.BOLD)
                sonAtilanmesaj.setTypeface(null,Typeface.BOLD)
                sonAtilanmesaj.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
                gonderilmeZamani.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))

            }else {
                okunduBilgisi.visibility=View.INVISIBLE
                userName.setTypeface(null,Typeface.NORMAL)
                sonAtilanmesaj.setTypeface(null,Typeface.NORMAL)
                gonderilmeZamani.setTextColor(ContextCompat.getColor(itemView.context,R.color.gri))
                sonAtilanmesaj.setTextColor(ContextCompat.getColor(itemView.context,R.color.gri))

            }

            tumLayout.setOnClickListener {

                var intent= Intent(itemView.context,chat::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("konusulacakKisi",oankiKonusmalar.user_id.toString())

                FirebaseDatabase.getInstance().getReference()
                    .child("konusmalar")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(oankiKonusmalar.user_id.toString())
                    .child("goruldu").setValue(true)
                    .addOnCompleteListener {
                        itemView.context.startActivity(intent)


                    }



            }

            tumLayout.setOnLongClickListener(View.OnLongClickListener {



                var alert = androidx.appcompat.app.AlertDialog.Builder(itemView.context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
                    .setTitle("MESAJI SİL ")
                    .setPositiveButton("SİL", object : DialogInterface.OnClickListener {

                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            var silinicekKonusma=oankiKonusmalar.user_id


                            mref.child("mesajlar").child(FirebaseAuth.getInstance().currentUser!!.uid!!).child(silinicekKonusma!!).addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.ref.removeValue()


                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })




                            mref.child("konusmalar").child(FirebaseAuth.getInstance().currentUser!!.uid!!).child(silinicekKonusma!!).addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.ref.removeValue()


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

                return@OnLongClickListener true


            })










            konusulanKisininBilgilerinigetir(oankiKonusmalar.user_id.toString())


        }


        private fun konusulanKisininBilgilerinigetir(userID: String) {



            var mref= FirebaseDatabase.getInstance().reference

             mref.child("users").child("kullanicilar").child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     if (snapshot.value !=null){



                             userName.text=snapshot.child("user_name").getValue().toString()
                             imageLoader.setImage(snapshot.child("user_detail").child("profile_picture").getValue().toString(),userpp,null,"")






                     }
                 }

                 override fun onCancelled(error: DatabaseError) {
                 }
             })

            mref.child("users").child("isletmeler").child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){



                        userName.text=snapshot.child("user_name").getValue().toString()
                        imageLoader.setImage(snapshot.child("user_detail").child("profile_picture").getValue().toString(),userpp,null,"")






                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }

        var tumLayout=itemView as ConstraintLayout
        var sonAtilanmesaj=tumLayout.sonMesaj_id
        var gonderilmeZamani=tumLayout.zamanOnce_id
        var userpp=tumLayout.img_konusmalarpp
        var userName=tumLayout.tv_username
        var okunduBilgisi=tumLayout.okundu_bilgisi


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        var view=LayoutInflater.from(myContext).inflate(R.layout.recycler_row_konusmalar,parent,false)
        return myViewHolder(view)


    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        holder.setdata(tumKonusmalar.get(position))
    }

    override fun getItemCount(): Int {
       return tumKonusmalar.size
    }
}