package com.example.paylasim.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.paylasim.R
import com.example.paylasim.login.RegisterActivity
import com.example.paylasim.home.MainActivity
import com.example.paylasim.models.kullanicilar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_email_onay.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference


        setupAuthLis()
        init()
    }

    private fun setupAuthLis() {

        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser

                if (user!=null){
                    var intent=Intent(this@LoginActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    startActivity(intent)
                    finish()

                }else{

                }

            }

        }

    }

    fun init() {

        tv_login.addTextChangedListener(watcher)
        tv_sifre.addTextChangedListener(watcher)



        btn_login.setOnClickListener {

            girisYapacakKullanici(tv_login.text.toString(), tv_sifre.text.toString())


        }









    }

    fun kaydolTv(view: View){
        val intent=Intent(this, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    private fun girisYapacakKullanici(emailPhoneNumberUserName: String, sifre: String) {

        var kullaniciBulundu=false

        mref.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot!!.getValue() != null ){
                    for (ds in snapshot!!.children) {

                        var okunanKullanici = ds.getValue(kullanicilar::class.java)

                        if (!okunanKullanici!!.email!!.isNullOrEmpty() && okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)) {

                            oturumAc(okunanKullanici, sifre, false)
                            kullaniciBulundu=true
                            break

                        } else if (!okunanKullanici!!.user_name!!.isNullOrEmpty() && okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)) {
                            oturumAc(okunanKullanici, sifre, false)
                            kullaniciBulundu=true
                            break
                        } else if (!okunanKullanici!!.phone_number!!.isNullOrEmpty() && okunanKullanici!!.phone_number!!.toString().equals(emailPhoneNumberUserName)) {

                            oturumAc(okunanKullanici, sifre, true)
                            kullaniciBulundu=true
                            break
                        }

                    }

                    if(kullaniciBulundu==false){
                        Toast.makeText(this@LoginActivity,"Kullanıcı Bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }

            }


        })


    }

    private fun oturumAc(okunanKullanici: kullanicilar, sifre: String, telefonlagiris: Boolean) {
        var girisYapacakEmail = ""

        if (telefonlagiris) {
            girisYapacakEmail = okunanKullanici.email_phone_number.toString()

        } else {
            girisYapacakEmail = okunanKullanici.email.toString()
        }

        auth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        Toast.makeText(
                            this@LoginActivity,"Hoşgeldiniz:" + okunanKullanici.user_name,Toast.LENGTH_LONG).show()


                    } else {

                        Toast.makeText(
                            this@LoginActivity,"Kullanıcı Adı/Şifre hatalı", Toast.LENGTH_LONG).show()

                    }
                }

            })

    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (tv_login.text.toString().length >= 6 && tv_sifre.text.toString().length >= 6) {
                btn_login.isEnabled = true
                btn_login.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.white))
                btn_login.setBackgroundColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.teal_700
                    )
                )

            } else {
                btn_login.isEnabled = false
                btn_login.setBackgroundColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.white
                    )
                )
                btn_login.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.black))


            }
        }


        override fun afterTextChanged(p0: Editable?) {
        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","logindesin")

        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        if (mauthLis!=null){
            auth.removeAuthStateListener(mauthLis)

        }
    }
}