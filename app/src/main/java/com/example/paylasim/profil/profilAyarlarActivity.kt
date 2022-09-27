package com.example.paylasim.profil

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.paylasim.R
import kotlinx.android.synthetic.main.activity_profil_ayarlar.*

class profilAyarlarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_ayarlar)

        imageView_back.setOnClickListener(){
            onBackPressed()

        }

        fragmentProfilEdit()

        sifre_degistir.setOnClickListener {

            profilAyarlarRoot.visibility= View.GONE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileAyarlarContainer,sifreDegistirFragment())
            transaction.commit()
            transaction.addToBackStack("sifre değistir")

        }


    }

    private fun fragmentProfilEdit() {
        profil_edit.setOnClickListener {
            profilAyarlarRoot.visibility= View.GONE
            var transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileAyarlarContainer,profilEditFragment())
            transaction.commit()
            transaction.addToBackStack("profil bilgileri düzenle")

        }


    }

    override fun onBackPressed() {
        profilAyarlarRoot.visibility=View.VISIBLE
        super.onBackPressed()
    }
}