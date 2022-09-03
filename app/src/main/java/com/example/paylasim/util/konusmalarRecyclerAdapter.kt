package com.example.paylasim.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.models.konusmalar
import com.example.paylasim.models.kullanicilar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.recycler_row_konusmalar.view.*

class konusmalarRecyclerAdapter(var tumKonusmalar:ArrayList<konusmalar>,var myContext: Context):RecyclerView.Adapter<konusmalarRecyclerAdapter.myViewHolder>() {


    class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun setdata(oankiKonusmalar: konusmalar) {

            sonAtilanmesaj.text=oankiKonusmalar.son_mesaj.toString()

            gonderilmeZamani.text=TimeAgo.getTimeAgoForComments(oankiKonusmalar.gonderilmeZamani!!.toLong())




            konusulanKisininBilgilerinigetir(oankiKonusmalar.user_id.toString())


        }

        private fun konusulanKisininBilgilerinigetir(userID: String) {

            var mref= FirebaseDatabase.getInstance().reference

             mref.child("users").child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
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