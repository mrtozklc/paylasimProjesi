package com.example.paylasim.util

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.models.yorumlar
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_yorumlar.*
import kotlinx.android.synthetic.main.fragment_yorumlar.circle_profilPhoto
import kotlinx.android.synthetic.main.fragment_yorumlar.et_mesajEkle
import kotlinx.android.synthetic.main.fragment_yorumlar.view.*
import kotlinx.android.synthetic.main.recycler_row_yorumlar.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class yorumlarFragment : Fragment() {

    var   yorumYapilacakGonderininID:String?=null
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var adapter: FirebaseRecyclerAdapter<yorumlar,YorumlarViewHolder>




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_yorumlar, container, false)

        mAuth= FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference.child("yorumlar").child(yorumYapilacakGonderininID!!)

        val options = FirebaseRecyclerOptions.Builder<yorumlar>()
            .setQuery(mRef, yorumlar::class.java)
            .build()

      adapter=object :FirebaseRecyclerAdapter<yorumlar,YorumlarViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YorumlarViewHolder {
                var yorumlarViewHolder=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_yorumlar, parent, false)

                return YorumlarViewHolder(yorumlarViewHolder)
            }

            override fun onBindViewHolder(holder: YorumlarViewHolder, position: Int, model: yorumlar) {

                holder.setData(model)

                if(position==0 && (yorumYapilacakGonderininID!!.equals(getRef(0).key))){
                    holder.yorumBegen.visibility=View.INVISIBLE
                }
                holder.begenme(yorumYapilacakGonderininID!!,getRef(position).key)
                holder.begenmeDurumu(yorumYapilacakGonderininID!!,getRef(position).key)


            }

        }
        view.recyclerviewYorumlar.layoutManager= LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        view.recyclerviewYorumlar.adapter=adapter





        view.tw_yorumPaylas.setOnClickListener {

            var yorum=et_mesajEkle.text.toString().trim()


            if(!TextUtils.isEmpty(yorum.toString())){

            var yeniYorum = hashMapOf<String, Any>(
                "user_id" to mUser.uid,
                "yorum" to et_mesajEkle.text.toString(),
                "yorum_begeni" to "0",
                "yorum_tarih" to ServerValue.TIMESTAMP
            )




            FirebaseDatabase.getInstance().getReference().child("yorumlar")
                .child(yorumYapilacakGonderininID!!).push().setValue(yeniYorum)


            et_mesajEkle.setText("")

            view.recyclerviewYorumlar.smoothScrollToPosition(view.recyclerviewYorumlar.adapter!!.itemCount)





        }
        }

        setupProfilPicture()

        view.img_backk.setOnClickListener {
            requireActivity().onBackPressed()
        }


        return view
    }

    class YorumlarViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var tumYorumlarLayoutu=itemView as ConstraintLayout
        var yorumYapanUserPhoto=tumYorumlarLayoutu.profilPhotoYorumlar
        var kullaniciAdiveYorum=tumYorumlarLayoutu.tv_aciklama
        var yorumBegen=tumYorumlarLayoutu.like
        var yorumSure=tumYorumlarLayoutu.tv_yorumZamani
        var yorumBegenmeSayisi=tumYorumlarLayoutu.tv_begeni

        fun setData(oanOlusturulanYorum:yorumlar) {


            yorumSure.setText(TimeAgo.getTimeAgoForComments(oanOlusturulanYorum!!.yorum_tarih!!))
            yorumBegenmeSayisi.setText(oanOlusturulanYorum.yorum_begeni)
            kullaniciAdiveYorum.setText(oanOlusturulanYorum.yorum)




            kullaniciBilgileriniGetir(oanOlusturulanYorum.user_id,oanOlusturulanYorum.yorum)





        }


        private fun kullaniciBilgileriniGetir(user_id: String?, yorum: String?) {

            var mref=FirebaseDatabase.getInstance().reference
            mref.child("users").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var userNameveYorum="<font color=#000>"+ snapshot!!.getValue(kullanicilar::class.java)!!.user_name!!.toString()+"</font>" + " " + yorum
                    var sonuc: Spanned?=null
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        sonuc= Html.fromHtml(userNameveYorum,Html.FROM_HTML_MODE_LEGACY)
                    }else {
                        sonuc=Html.fromHtml(userNameveYorum)
                    }
                    kullaniciAdiveYorum.setText(sonuc)
                    imageLoader.setImage(snapshot!!.getValue(kullanicilar::class.java)!!.user_detail!!.profile_picture!!.toString(),yorumYapanUserPhoto,null,"")
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

        fun begenme(yorumYapilacakGonderininID: String, begenilecekYorumId: String?) {

            var mRef=FirebaseDatabase.getInstance().reference.child("yorumlar").child(yorumYapilacakGonderininID).child(begenilecekYorumId!!)


            yorumBegen.setOnClickListener {


                mRef.child("begenenler").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot!!.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                            mRef.child("begenenler").child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                            yorumBegen.setImageResource(R.drawable.ic_launcher_like_foreground)


                        }else{
                            mRef.child("begenenler").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(FirebaseAuth.getInstance().currentUser!!.uid)

                            yorumBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)



                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }

        }

        fun begenmeDurumu(yorumYapilacakGonderininID: String, begenilecekYorumId: String?) {
            var mRef=FirebaseDatabase.getInstance().reference.child("yorumlar").child(yorumYapilacakGonderininID).child(begenilecekYorumId!!)


            mRef.child("begenenler").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot!!.exists()){
                        yorumBegenmeSayisi.visibility=View.VISIBLE
                        yorumBegenmeSayisi.text=snapshot!!.childrenCount.toString()+" beğenme"

                    }else{
                        yorumBegenmeSayisi.visibility=View.INVISIBLE


                    }

                    if (snapshot!!.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                        yorumBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)


                    }else{
                        yorumBegen.setImageResource(R.drawable.ic_launcher_like_foreground)



                    }


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }
    }


    @Subscribe(sticky = true)
    internal fun onYorumYapilacakGonderi(gonderi: EventbusData.YorumYapilacakGonderininIDsiniGonder) {
        yorumYapilacakGonderininID = gonderi!!.gonderiID!!


    }
    private fun setupProfilPicture() {

        mRef=FirebaseDatabase.getInstance().reference.child("users")
        mRef.child(mUser.uid).child("user_detail").addListenerForSingleValueEvent(object : ValueEventListener{




            override fun onDataChange(snapshot: DataSnapshot) {
                var profilPictureURL=snapshot!!.child("profile_picture").getValue().toString()
                imageLoader.setImage(profilPictureURL,circle_profilPhoto,null,"")                }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}