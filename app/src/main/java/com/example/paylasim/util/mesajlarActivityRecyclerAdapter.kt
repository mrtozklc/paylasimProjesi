package com.example.paylasim.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasim.R
import com.example.paylasim.models.mesaj
import kotlinx.android.synthetic.main.activity_chat.view.*
import kotlinx.android.synthetic.main.mesaj_gonderen_recycler_row.view.*

class mesajlarActivityRecyclerAdapter(var tumMesajlar:ArrayList<mesaj>,var myContext:Context): RecyclerView.Adapter<mesajlarActivityRecyclerAdapter.mesajlarViewHolder>() {

    class mesajlarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        fun setData(anlikMesaj: mesaj) {
            tvmesajGonderen.text=anlikMesaj.mesaj


        }

        var tumLayaout=itemView as ConstraintLayout
        var tvmesajGonderen=tumLayaout.tv_mesajgonderen


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mesajlarActivityRecyclerAdapter.mesajlarViewHolder {

        var view=LayoutInflater.from(myContext).inflate(R.layout.mesaj_gonderen_recycler_row,parent,false)
        return mesajlarViewHolder(view)

    }

    override fun onBindViewHolder(holder: mesajlarActivityRecyclerAdapter.mesajlarViewHolder, position: Int
    ) {

        holder.setData(tumMesajlar.get(position))
    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }
}