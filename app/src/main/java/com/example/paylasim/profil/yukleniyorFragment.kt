package com.example.paylasim.profil

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.paylasim.R
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import kotlinx.android.synthetic.main.fragment_yukleniyor.view.*


class yukleniyorFragment :DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_yukleniyor, container, false)
       view.progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(requireActivity(),R.color.black),PorterDuff.Mode.SRC_IN)
        return view
    }

}