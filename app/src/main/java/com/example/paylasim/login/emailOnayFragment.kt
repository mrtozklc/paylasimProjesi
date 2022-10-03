package com.example.paylasim.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.paylasim.util.EventbusData
import com.example.paylasim.R
import com.example.paylasim.models.kullaniciDetaylari
import com.example.paylasim.models.kullanicilar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_email_onay.*
import kotlinx.android.synthetic.main.fragment_email_onay.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class emailOnayFragment : Fragment() {
    var gelenEmail = ""
    var telNo = ""
    var verificationID = ""
    var GelenCode = ""
    var sifre = ""
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    var emailleKayit = true




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_email_onay, container, false)

        auth = Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        if (auth.currentUser != null) {
            auth.signOut()
        }
        view.tvKaydoll.setOnClickListener {
            val intent = Intent(
                activity,
                LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }


        view.etAdSoyad.addTextChangedListener(watcher)
        view.etKullaniciAdi.addTextChangedListener(watcher)
        view.etSifre.addTextChangedListener(watcher)

        view.btn_giris.setOnClickListener {
            if( !view.etAdSoyad.text.toString().trim().isEmpty()){

            if(view.etKullaniciAdi.text.toString().trim().length>5 && view.etSifre.text.toString().trim().length>5 && !view.etAdSoyad.text.toString().trim().isNullOrEmpty())
            {
                var userNameKullanimdaMi = false

                mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot!!.getValue() != null) {


                            for (user in snapshot!!.children) {
                                var okunanKullanici = user.getValue(kullanicilar::class.java)
                                if (okunanKullanici!!.user_name!!.equals(view.etKullaniciAdi.text.toString())) {
                                    Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                    userNameKullanimdaMi = true
                                    break
                                }
                                mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot!!.getValue()!=null){
                                            for (user in snapshot!!.children){
                                                var okunanKullanici = user.getValue(kullanicilar::class.java)
                                                if (okunanKullanici!!.user_name!!.equals(view.etKullaniciAdi.text.toString())) {
                                                    Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                                    userNameKullanimdaMi = true
                                                    break
                                                }


                                            }
                                        }
                                        if (!userNameKullanimdaMi) {



                                            //kullanıcı email ile kayıt
                                            if (emailleKayit) {

                                                var sifre = view.etSifre.text.toString()
                                                var adSoyad = view.etAdSoyad.text.toString()
                                                var userName = view.etKullaniciAdi.text.toString()


                                                auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                                        override fun onComplete(p0: Task<AuthResult>) {

                                                            if (p0!!.isSuccessful) {
                                                                fcmTokenAl()

                                                                var userID = auth.currentUser!!.uid.toString()


                                                                var kaydedilecekKullaniciDetaylari=kullaniciDetaylari("0","0","0","","","","",null,null)

                                                                var kaydedilecekKullanici = kullanicilar(gelenEmail, sifre, userName, adSoyad, "", "", userID,kaydedilecekKullaniciDetaylari)

                                                                mref.child("users").child("kullanicilar").child(userID).setValue(kaydedilecekKullanici)
                                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                                        override fun onComplete(p0: Task<Void>) {
                                                                            if (p0!!.isSuccessful) {


                                                                            } else {

                                                                                auth.currentUser!!.delete()
                                                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                                                        override fun onComplete(p0: Task<Void>) {
                                                                                            if (p0!!.isSuccessful) {
                                                                                                Toast.makeText(activity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                                                            }
                                                                                        }

                                                                                    })
                                                                            }
                                                                        }


                                                                    })


                                                            } else {

                                                                Toast.makeText(activity, "Oturum açılamadı :" + p0!!.exception, Toast.LENGTH_SHORT).show()
                                                            }

                                                        }

                                                    })

                                            }




                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })
                            }



                        }
                        //veritabanında kullanıcı yok, aynen kaydet
                        else{
                            if (emailleKayit) {
                                var sifre = view.etSifre.text.toString()
                                var adSoyad = view.etAdSoyad.text.toString()
                                var userName = view.etKullaniciAdi.text.toString()


                                auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                        override fun onComplete(p0: Task<AuthResult>) {

                                            if (p0!!.isSuccessful) {
                                                fcmTokenAl()

                                                var userID = auth.currentUser!!.uid.toString()


                                                //oturum açan kullanıcın verilerini databaseye kaydedelim...
                                                var kaydedilecekKullaniciDetaylari = kullaniciDetaylari("0", "0", "0", "", "", "","",null,null)
                                                var kaydedilecekKullanici = kullanicilar(gelenEmail, sifre, userName, adSoyad, "", "", userID,kaydedilecekKullaniciDetaylari)

                                                mref.child("users").child("kullanicilar").child(userID).setValue(kaydedilecekKullanici)
                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                        override fun onComplete(p0: Task<Void>) {
                                                            if (p0!!.isSuccessful) {


                                                            } else {

                                                                auth.currentUser!!.delete()
                                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                                        override fun onComplete(p0: Task<Void>) {
                                                                            if (p0!!.isSuccessful) {
                                                                                Toast.makeText(activity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }

                                                                    })
                                                            }
                                                        }


                                                    })


                                            }

                                        }

                                    })

                            }





                        }


                    }


                })
            }else{
                Toast.makeText(activity,"Kullanıcı adı ve şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
            }
            }
            else{
                Toast.makeText(activity,"Ad ve soyad boş bırakılamaz.", Toast.LENGTH_SHORT).show()
            }



        }





        return view
    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (etAdSoyad.text.toString().length >=1 && etKullaniciAdi.text.toString().length >=1 && etSifre.text.toString().length >=1) {
                    btn_giris.isEnabled = true
                    btn_giris.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    btn_giris.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.teal_700
                        )
                    )

                } else {

                    btn_giris.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                    btn_giris.setTextColor(ContextCompat.getColor(activity!!, R.color.black))


                }



        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    @Subscribe(sticky = true)

    internal fun onKayitEvent(kayitbilgileri: EventbusData.kayitBilgileriniGonder) {

        if (kayitbilgileri.emailkayit == true) {
            emailleKayit = true
            gelenEmail = kayitbilgileri.email!!

            // Toast.makeText(activity, "Gelen email : " + gelenEmail, Toast.LENGTH_SHORT).show()
            Log.e("murat", "Gelen email : " + gelenEmail)
        } else {
            emailleKayit = false
            telNo = kayitbilgileri.telNo!!
            verificationID = kayitbilgileri.verificationID!!
            GelenCode = kayitbilgileri.code!!
            Log.e("murat", "Gelen telefon : " + telNo)



        }


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
                    FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.getValue() != null) {
                                for (user in snapshot!!.children) {


                                    var okunanKullanici = user.getValue(kullanicilar::class.java)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}