package com.example.paylasim.bildirimler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasim.R
import com.example.paylasim.models.bildirimModel
import com.example.paylasim.util.bildirimRecyclerAdapter
import com.example.paylasim.util.mainActivityRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_bildirim.*
import kotlinx.android.synthetic.main.activity_main.*

class bildirimActivity : AppCompatActivity() {
    var tumBildirimler= ArrayList<bildirimModel>()
    lateinit var mrecycler:RecyclerView
    lateinit var mlinear:LinearLayoutManager
    lateinit var mref:DatabaseReference
    lateinit var mauth:FirebaseAuth
    lateinit var recyclerviewadapter:bildirimRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bildirim)

        mauth=FirebaseAuth.getInstance()
        mref=FirebaseDatabase.getInstance().reference

        bildirimleriGetir()




        refresh_id.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                tumBildirimler.clear()
                recyclerviewadapter.notifyDataSetChanged()
                bildirimleriGetir()
                refresh_id.isRefreshing=false
            }

        })
    }

    private fun bildirimleriGetir() {
        progressBarBildirim.visibility= View.VISIBLE
        recyclerBildirim.visibility=View.INVISIBLE

        mref.child("bildirimler").child(mauth.currentUser!!.uid).orderByChild("time").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.getValue()!=null){



                    for (ds in snapshot.children){
                        var okunanBildirim=ds.getValue(bildirimModel::class.java)


                        tumBildirimler.add(okunanBildirim!!)
                    }
                    recyclerAdapter()

                }else{
                    progressBarBildirim.visibility= View.GONE
                    recyclerBildirim.visibility=View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun recyclerAdapter() {
        mrecycler=recyclerBildirim
        mlinear= LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mrecycler.layoutManager=mlinear

        recyclerviewadapter= bildirimRecyclerAdapter(this,tumBildirimler)
        mrecycler.adapter=recyclerviewadapter

        object :CountDownTimer(1000,1000){
            override fun onTick(p0: Long) {
                progressBarBildirim.visibility=View.GONE
                mrecycler.visibility=View.VISIBLE
            }

            override fun onFinish() {
            }

        }.start()

    }
}