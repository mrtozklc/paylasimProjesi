package com.example.paylasim.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object bildirimler {

    var mref=FirebaseDatabase.getInstance().reference
    var mauth=FirebaseAuth.getInstance()
    var muser= mauth.currentUser!!.uid

    val KAMPANYA_BEGENILDI=1
    val YORUM_YAPILDI=2
    val KAMPANYA_BEGENILDI_GERI=3

    fun bildirimKaydet(bildirimYapanUserID:String,bildirimTuru:Int,gonderiID:String){
        
        when(bildirimTuru){

            KAMPANYA_BEGENILDI->{

                var yeniBildirimID=mref.child("bildirimler").child(bildirimYapanUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", KAMPANYA_BEGENILDI)
                yeniBildirim.put("user_id", muser)
                yeniBildirim.put("gonderi_id",gonderiID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                mref.child("bildirimler").child(bildirimYapanUserID).child(yeniBildirimID!!).setValue(yeniBildirim)


            }


            YORUM_YAPILDI->{
                var yeniBildirimID=mref.child("bildirimler").child(bildirimYapanUserID).push().key
                var yeniBildirim=HashMap<String,Any>()
                yeniBildirim.put("bildirim_tur", YORUM_YAPILDI)
                yeniBildirim.put("user_id", muser)
                yeniBildirim.put("gonderi_id",gonderiID)
                yeniBildirim.put("time", ServerValue.TIMESTAMP)
                mref.child("bildirimler").child(bildirimYapanUserID).child(yeniBildirimID!!).setValue(yeniBildirim)
            }

            KAMPANYA_BEGENILDI_GERI->{

                mref.child("bildirimler").child(bildirimYapanUserID).orderByChild("gonderi_id").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (bildirim in snapshot!!.children){

                            var okunanBildirimKey=bildirim!!.key

                            if(bildirim.child("bildirim_tur").getValue().toString().toInt() == KAMPANYA_BEGENILDI && bildirim.child("gonderi_id").getValue()!!.equals(gonderiID)){
                                mref.child("bildirimler").child(bildirimYapanUserID).child(okunanBildirimKey!!).removeValue()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }



        }
    }


}