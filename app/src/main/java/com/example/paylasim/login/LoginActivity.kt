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
import com.example.paylasim.mesajlar.chat
import com.example.paylasim.mesajlar.mesajlar
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.service.messagingService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*

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

        mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot!!.getValue() != null ){
                    for (ds in snapshot!!.children) {

                        var okunanKullanici = ds.getValue(kullanicilar::class.java)

                        if (!okunanKullanici!!.email!!.isNullOrEmpty() && okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)) {

                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break

                        } else if (!okunanKullanici!!.user_name!!.isNullOrEmpty() && okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)) {
                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break
                        }

                    }


                }

                mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot!!.getValue() != null ){
                            for (ds in snapshot!!.children) {

                                var okunanKullanici = ds.getValue(kullanicilar::class.java)

                                if (!okunanKullanici!!.email!!.isNullOrEmpty() && okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)) {

                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break

                                } else if (!okunanKullanici!!.user_name!!.isNullOrEmpty() && okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)) {
                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break
                                }

                            }

                            if(kullaniciBulundu==false){
                                Toast.makeText(this@LoginActivity,"Kullanıcı bulunamadı.Lütfen üye ol.", Toast.LENGTH_SHORT).show()


                            }
                        }else{
                            Toast.makeText(this@LoginActivity,"Kullanıcı bulunamadı.Lütfen üye ol.", Toast.LENGTH_SHORT).show()


                        }

                    }


                })



            }


        })




    }

    private fun oturumAc(okunanKullanici: kullanicilar, sifre: String) {

        var girisYapacakEmail = ""


            girisYapacakEmail = okunanKullanici.email.toString()


        auth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {


                        fcmTokenAl()
                        Toast.makeText(
                            this@LoginActivity,"Hoşgeldiniz:" + okunanKullanici.user_name,Toast.LENGTH_LONG).show()


                    } else {

                        Toast.makeText(
                            this@LoginActivity,"Kullanıcı Adı/Şifre hatalı", Toast.LENGTH_LONG).show()

                    }
                }

            })

    }

    private fun fcmTokenAl() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            var token=task.result

            newTokenAl(token)



        })
    }


    private fun newTokenAl(newToken: String) {

        if (FirebaseAuth.getInstance().currentUser!=null){

            FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!!.getValue() != null) {

                        for (user in snapshot!!.children) {


                            var okunanKullanici = user.getValue(kullanicilar::class.java)
                            if (okunanKullanici!!.user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {

                                FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").child(FirebaseAuth.getInstance().currentUser!!.uid).child("FCM_TOKEN").setValue(newToken)



                            }
                        }
                    }
                    FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.getValue() != null) {

                                for (user in snapshot!!.children) {


                                    var okunanKullanici = user.getValue(kullanicilar::class.java)
                                    Log.e("newtoken","okunankullanici"+okunanKullanici)

                                    if (okunanKullanici!!.user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {

                                        FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid).child("FCM_TOKEN").setValue(newToken)



                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }

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