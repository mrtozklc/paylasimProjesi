package com.example.paylasim.login

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
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
import com.example.paylasim.R
import com.example.paylasim.models.kullaniciDetaylari
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.util.EventbusData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_isletme_kayit_bilgileri.*
import kotlinx.android.synthetic.main.fragment_isletme_kayit_bilgileri.view.*
import kotlinx.android.synthetic.main.fragment_isletme_kayit_bilgileri.view.tvKaydoll
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException
import java.util.*


class IsletmeKayitBilgileriFragment : Fragment() {

    var gelenEmail = ""
    var telNo = ""
    var verificationID = ""
    var GelenCode = ""
    var sifre = ""
    var adress=""
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    var emailleKayit = true



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_isletme_kayit_bilgileri, container, false)



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

        view.etAdSoyadISletme.addTextChangedListener(watcher)
        view.etKullaniciAdiISletme.addTextChangedListener(watcher)
        view.etSifreIsletme.addTextChangedListener(watcher)
        view.et_adresIsletme.addTextChangedListener(watcher)
        view.et_telefonIsletme.addTextChangedListener(watcher)


        view.btn_girisISletme.setOnClickListener{
            if(view.etKullaniciAdiISletme.text.toString().trim().length>5 && view.etSifreIsletme.text.toString().trim().length>5 && !view.etAdSoyadISletme.text.toString().trim().isNullOrEmpty()){
                if (view.et_telefonIsletme.text.toString().trim().length==10){
                    var userNameKullanimdaMi = false
                    mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot!!.getValue() != null) {

                                for (user in snapshot!!.children) {
                                    var okunanKullanici = user.getValue(kullanicilar::class.java)
                                    if (okunanKullanici!!.user_name!!.equals(view.etKullaniciAdiISletme.text.toString())) {
                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                        userNameKullanimdaMi = true
                                        break
                                    }
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot!!.getValue()!=null){
                                                for (user in snapshot!!.children ){
                                                    var okunanKullanici = user.getValue(kullanicilar::class.java)
                                                    if (okunanKullanici!!.user_name!!.equals(view.etKullaniciAdiISletme.text.toString())) {
                                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                                        userNameKullanimdaMi = true
                                                        break
                                                    }

                                                }
                                            }
                                            if (!userNameKullanimdaMi) {



                                                //kullanıcı email ile kayıt
                                                if (emailleKayit) {

                                                    var sifre = view.etSifreIsletme.text.toString()
                                                    var adSoyad = view.etAdSoyadISletme.text.toString()
                                                    var userName = view.etKullaniciAdiISletme.text.toString()
                                                    var adres=view.et_adresIsletme.text.toString()
                                                    var telefon=view.et_telefonIsletme.text.toString()




                                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                                        .addOnCompleteListener(object :
                                                            OnCompleteListener<AuthResult> {
                                                            override fun onComplete(p0: Task<AuthResult>) {

                                                                if (p0!!.isSuccessful) {

                                                                    var userID = auth.currentUser!!.uid.toString()

                                                                    getAddressFromLocation(adres,context,userID)


                                                                    var kaydedilecekKullaniciDetaylari=
                                                                        kullaniciDetaylari("0","0","0","","","",adres,null ,null)

                                                                    var kaydedilecekKullanici = kullanicilar(gelenEmail, sifre, userName, adSoyad, telefon, "",userID,kaydedilecekKullaniciDetaylari)

                                                                    mref.child("users").child("isletmeler").child(userID).setValue(kaydedilecekKullanici)
                                                                        .addOnCompleteListener(object :
                                                                            OnCompleteListener<Void> {
                                                                            override fun onComplete(p0: Task<Void>) {
                                                                                if (p0!!.isSuccessful) {

                                                                                } else {

                                                                                    auth.currentUser!!.delete()
                                                                                        .addOnCompleteListener(object :
                                                                                            OnCompleteListener<Void> {
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

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })
                                }



                            }
                            //veritabanında kullanıcı yok, aynen kaydet
                            else{
                                if (emailleKayit) {
                                    var sifre = view.etSifreIsletme.text.toString()
                                    var adSoyad = view.etAdSoyadISletme.text.toString()
                                    var userName = view.etKullaniciAdiISletme.text.toString()
                                    var adres=view.et_adresIsletme.text.toString()
                                    var telefon=view.et_telefonIsletme.text.toString()






                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                        .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                            override fun onComplete(p0: Task<AuthResult>) {

                                                if (p0!!.isSuccessful) {

                                                    var userID = auth.currentUser!!.uid.toString()

                                                    getAddressFromLocation(
                                                        adres,
                                                        context,userID)

                                                    //oturum açan kullanıcın verilerini databaseye kaydet

                                                    var kaydedilecekKullaniciDetaylari = kullaniciDetaylari("0", "0", "0", "", "", "",adres,null,null)
                                                    var kaydedilecekKullanici = kullanicilar(gelenEmail, sifre, userName, adSoyad, telefon, "", userID, kaydedilecekKullaniciDetaylari)

                                                    mref.child("users").child("isletmeler").child(userID).setValue(kaydedilecekKullanici)
                                                        .addOnCompleteListener(object :
                                                            OnCompleteListener<Void> {
                                                            override fun onComplete(p0: Task<Void>) {
                                                                if (p0!!.isSuccessful) {

                                                                } else {

                                                                    auth.currentUser!!.delete()
                                                                        .addOnCompleteListener(object :
                                                                            OnCompleteListener<Void> {
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
                    Toast.makeText(activity,"Lütfen telefon numarasını 10 hane şeklinde giriniz.", Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(activity,"Kullanıcı adı ve şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }


    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (etAdSoyadISletme.text.toString().length >=1 && etKullaniciAdiISletme.text.toString().length >=1 && etSifreIsletme.text.toString().length >=1&&et_adresIsletme.text.toString().length>=1&&et_telefonIsletme.text.toString().length>=1) {
                    btn_girisISletme.isEnabled = true
                    btn_girisISletme.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                    btn_girisISletme.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.teal_700
                        )
                    )

                } else {

                    btn_girisISletme.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                    btn_girisISletme.setTextColor(ContextCompat.getColor(activity!!, R.color.black))


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
        }





    }

   /* private inner class GeocoderHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            locationAddress = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address")

                }
                else -> null
            }



            mref.child("konumlar").setValue(locationAddress)

            Log.e("gelen konum","locatiom"+locationAddress)
        }
    }*/

    fun getAddressFromLocation(
        locationAddress: String,
        context: Context?,
        userID:String,
    ) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList: List<*>? = geocoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0] as Address

                        Log.e("adres","gelen"+address)

                        var latitude=address.latitude
                        var longitude=address.longitude

                        mref.child("users").child("isletmeler").child(userID).child("user_detail").child("latitude").setValue(latitude)
                        mref.child("users").child("isletmeler").child(userID).child("user_detail").child("longitude").setValue(longitude)





                    }
                } catch (e: IOException) {
                } finally {

                    if (result != null) {

                    } else {

                    }

                }
            }
        }
        thread.start()
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