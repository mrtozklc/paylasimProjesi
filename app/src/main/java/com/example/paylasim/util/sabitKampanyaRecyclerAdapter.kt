package com.example.paylasim.util

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.example.paylasim.R
import com.example.paylasim.models.sabitKampanya
import com.example.paylasim.util.bildirimRecyclerAdapter.viewHolder
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import kotlinx.android.synthetic.main.recycler_row_sabit_kampanyalar.view.*


class sabitKampanyaRecyclerAdapter(var context: Context, var tumSabitKampanyalar:ArrayList<sabitKampanya>):

    SliderViewAdapter<sabitKampanyaRecyclerAdapter.sabitKampanyalarViewHolder>() {

    private val mSliderItems= ArrayList<sabitKampanya>()

    class sabitKampanyalarViewHolder(itemView:View):SliderViewAdapter.ViewHolder(itemView) {

        var tumLayout =itemView as FrameLayout
        var gonderi=tumLayout.iv_auto_image_slider
        var aciklama=tumLayout.tv_auto_image_slider
        var gif=tumLayout.iv_gif_container




        fun setData(position: Int, sabitKampanya: sabitKampanya) {

           aciklama.setText(sabitKampanya.aciklama);
            aciklama.setTextSize(22F);
            aciklama.setTextColor(Color.WHITE);


            Picasso.get().load(sabitKampanya.file_url).fit().into(gonderi)

           gonderi.setOnClickListener(View.OnClickListener {
                Toast.makeText(
                    itemView.context,
                    "This is item in position $position",
                    Toast.LENGTH_SHORT
                ).show()
            })


        }


    }



    override fun onBindViewHolder(
        holder: sabitKampanyaRecyclerAdapter.sabitKampanyalarViewHolder,
        position: Int
    ) {


        holder.setData(position, tumSabitKampanyalar[position])

    }





    override fun getCount(): Int {
        return tumSabitKampanyalar.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): sabitKampanyalarViewHolder {

        var viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row_sabit_kampanyalar, parent, false)



        return sabitKampanyalarViewHolder(viewHolder)


    }
}