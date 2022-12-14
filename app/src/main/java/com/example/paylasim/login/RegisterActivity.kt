package com.example.paylasim.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.paylasim.R
import com.example.paylasim.home.MainActivity
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.util.EventbusData
import com.example.paylasim.util.imageLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    lateinit var mmanager: FragmentManager
    lateinit var mref:DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var mauthLis:FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupAuthLis()
        initImageLoader()


        mref = FirebaseDatabase.getInstance().reference
        auth= FirebaseAuth.getInstance()
        mmanager = supportFragmentManager

        mmanager.addOnBackStackChangedListener(this)



        register()

    }


    private fun register() {

        

        textView3.setOnClickListener {
            val intent =
                Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        textView_telefon.setOnClickListener() {
            view2.visibility = View.VISIBLE
            view_eposta.visibility = View.INVISIBLE
            editTextTextPersonName.setText("")
            editTextTextPersonName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            editTextTextPersonName.setHint("e-posta")
            btn_kayit.isEnabled = false


        }

        textView_eposta.setOnClickListener() {

            view2.visibility = View.INVISIBLE
            view_eposta.visibility = View.VISIBLE
            editTextTextPersonName.setText("")
            editTextTextPersonName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            editTextTextPersonName.setHint("E-POSTA")
            btn_kayit.isEnabled = false


        }

        editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 10) {
                    btn_kayit.isEnabled = true
                    btn_kayit.setTextColor(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.white
                        )
                    )
                    btn_kayit.setBackgroundColor(
                        ContextCompat.getColor(
                            this@RegisterActivity,
                            R.color.teal_700
                        )
                    )


                } else {
                    btn_kayit.isEnabled = false


                }


            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        btn_kayit.setOnClickListener {

            if(editTextTextPersonName.hint.toString().equals("e-posta")){


                if (checkMail(editTextTextPersonName.text.toString())) {

                    var emailKullanimdaMi = false


                    mref.child("users").child("isletmeler")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot!!.getValue() != null) {

                                    for (user in snapshot!!.children) {


                                        var okunanKullanici = user.getValue(kullanicilar::class.java)
                                        if (okunanKullanici!!.email!!.equals(editTextTextPersonName.text.toString())) {


                                            Log.e("kullanicilar??n","i??letmeb??l??m??"+okunanKullanici)



                                            Toast.makeText(this@RegisterActivity, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()


                                            emailKullanimdaMi = true
                                            break
                                        }




                                    }
                                        //i??letme de??il kullanicilara bak
                                        mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot!!.getValue()!=null){
                                                    for (user in snapshot.children){

                                                        var okunanKullanici = user.getValue(kullanicilar::class.java)

                                                        if (okunanKullanici!!.email!!.equals(editTextTextPersonName.text.toString())) {

                                                            Log.e("kullanicilar??n","kullanicib??l??m??"+okunanKullanici)


                                                            Toast.makeText(this@RegisterActivity, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                            emailKullanimdaMi = true
                                                            break

                                                        }

                                                    } // i??letme var kullanici var kullanici bulunamad??,kaydet
                                                    if (!emailKullanimdaMi){
                                                        loginroot.visibility = View.GONE
                                                        loginframe.visibility = View.VISIBLE
                                                        var transaction =
                                                            supportFragmentManager.beginTransaction()
                                                        transaction.replace(
                                                            R.id.loginframe,
                                                            kullaniciBilgileriFragment()
                                                        )
                                                        transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                        transaction.commit()
                                                        EventBus.getDefault().postSticky(
                                                            EventbusData.kayitBilgileriniGonder(
                                                                null,
                                                                editTextTextPersonName.text.toString(),
                                                                null,
                                                                null,
                                                                true
                                                            )
                                                        )


                                                    }
                                                }
                                                if(!emailKullanimdaMi){

                                                    Log.e("veritaban??nda i??letme var,kullanici yok ancak kay??tl?? de??il,kaydet","??al??st??")


                                                    loginroot.visibility = View.GONE
                                                    loginframe.visibility = View.VISIBLE
                                                    var transaction = supportFragmentManager.beginTransaction()
                                                    transaction.replace(
                                                        R.id.loginframe,
                                                        kullaniciBilgileriFragment()
                                                    )
                                                    transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                    transaction.commit()
                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            editTextTextPersonName.text.toString(),
                                                            null,
                                                            null,
                                                            true
                                                        )
                                                    )

                                                }

                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                            }

                                        })





                                }

                                //veritaban??nda hi?? i??letme  yok, kullanicilara bak
                                else{

                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot!!.getValue()!=null){
                                                for (user in snapshot!!.children){


                                                    var okunanKullanici = user.getValue(kullanicilar::class.java)
                                                    if (okunanKullanici!!.email!!.equals(editTextTextPersonName.text.toString())) {

                                                        Log.e("i??letme yok","kullanici var,bulunan kullanici"+okunanKullanici)
                                                        Toast.makeText(this@RegisterActivity, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                        emailKullanimdaMi = true
                                                        break
                                                    }



                                                }
                                                //veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet
                                                if(!emailKullanimdaMi)
                                                {
                                                    Log.e("veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet","??al??st??")


                                                    loginroot.visibility = View.GONE
                                                    loginframe.visibility = View.VISIBLE
                                                    var transaction = supportFragmentManager.beginTransaction()
                                                    transaction.replace(
                                                        R.id.loginframe,
                                                        kullaniciBilgileriFragment()
                                                    )
                                                    transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                    transaction.commit()
                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            editTextTextPersonName.text.toString(),
                                                            null,
                                                            null,
                                                            true
                                                        )
                                                    )
                                                }
                                            }
                                            if  (!emailKullanimdaMi)
                                            //veritaban??nda  i??letme ve kullanici yok direkt kaydet
                                            {
                                                Log.e("elsekkaydet","direktkaydetcal??st??")

                                                Log.e("veritaban??nda kullanicida yok direkt kaydet","??al??st??")



                                                loginroot.visibility = View.GONE
                                                loginframe.visibility = View.VISIBLE
                                                var transaction = supportFragmentManager.beginTransaction()
                                                transaction.replace(
                                                    R.id.loginframe,
                                                    kullaniciBilgileriFragment()
                                                )
                                                transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                transaction.commit()
                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        editTextTextPersonName.text.toString(),
                                                        null,
                                                        null,
                                                        true
                                                    )
                                                )

                                            }



                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })



                                }

                            }

                        })


                } else {
                    Toast.makeText(
                        this,
                        "L??tfen ge??erli bir E-mail  giriniz",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            else {



                    if (checkMail(editTextTextPersonName.text.toString())) {

                        var emailKullanimdaMi = false


                        mref.child("users").child("isletmeler")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    if (snapshot!!.getValue() != null) {

                                        for (user in snapshot!!.children) {


                                            var okunanKullanici = user.getValue(kullanicilar::class.java)
                                            if (okunanKullanici!!.email!!.equals(editTextTextPersonName.text.toString())) {


                                                Log.e("kullanicilar??n","i??letmeb??l??m??"+okunanKullanici)



                                                Toast.makeText(this@RegisterActivity, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()


                                                emailKullanimdaMi = true
                                                break
                                            }




                                        }
                                        //i??letme de??il kullanicilara bak
                                        mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot!!.getValue()!=null){
                                                    for (user in snapshot.children){

                                                        var okunanKullanici = user.getValue(kullanicilar::class.java)

                                                        if (okunanKullanici!!.email!!.equals(editTextTextPersonName.text.toString())) {

                                                            Log.e("kullanicilar??n","kullanicib??l??m??"+okunanKullanici)


                                                            Toast.makeText(this@RegisterActivity, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                            emailKullanimdaMi = true
                                                            break

                                                        }

                                                    } // kullanici bulunamad??,kaydet
                                                    if (!emailKullanimdaMi){
                                                        loginroot.visibility = View.GONE
                                                        loginframe.visibility = View.VISIBLE
                                                        var transaction =
                                                            supportFragmentManager.beginTransaction()
                                                        transaction.replace(
                                                            R.id.loginframe,
                                                            IsletmeKayitBilgileriFragment()
                                                        )
                                                        transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                        transaction.commit()
                                                        EventBus.getDefault().postSticky(
                                                            EventbusData.kayitBilgileriniGonder(
                                                                null,
                                                                editTextTextPersonName.text.toString(),
                                                                null,
                                                                null,
                                                                true
                                                            )
                                                        )


                                                    }
                                                }
                                                if(!emailKullanimdaMi){

                                                    Log.e("veritaban??nda i??letme var,kullanici yok ancak kay??tl?? de??il,kaydet","??al??st??")


                                                    loginroot.visibility = View.GONE
                                                    loginframe.visibility = View.VISIBLE
                                                    var transaction = supportFragmentManager.beginTransaction()
                                                    transaction.replace(
                                                        R.id.loginframe,
                                                        IsletmeKayitBilgileriFragment()
                                                    )
                                                    transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                    transaction.commit()
                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            editTextTextPersonName.text.toString(),
                                                            null,
                                                            null,
                                                            true
                                                        )
                                                    )

                                                }

                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                            }

                                        })





                                    }

                                    //veritaban??nda hi?? i??letme  yok, kullanicilara bak
                                    else{

                                        mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
                                            override fun onDataChange(snapshot: DataSnapshot) {

                                                if (snapshot!!.getValue()!=null){
                                                    for (user in snapshot!!.children){


                                                        var okunanKullanici = user.getValue(kullanicilar::class.java)
                                                        if (okunanKullanici!!.email!!.equals(editTextTextPersonName.text.toString())) {

                                                            Log.e("i??letme yok","kullanici var,bulunan kullanici"+okunanKullanici)
                                                            Toast.makeText(this@RegisterActivity, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                            emailKullanimdaMi = true
                                                            break
                                                        }



                                                    }
                                                    //veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet
                                                    if(!emailKullanimdaMi)
                                                    {
                                                        Log.e("veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet","??al??st??")


                                                        loginroot.visibility = View.GONE
                                                        loginframe.visibility = View.VISIBLE
                                                        var transaction = supportFragmentManager.beginTransaction()
                                                        transaction.replace(
                                                            R.id.loginframe,
                                                            IsletmeKayitBilgileriFragment()
                                                        )
                                                        transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                        transaction.commit()
                                                        EventBus.getDefault().postSticky(
                                                            EventbusData.kayitBilgileriniGonder(
                                                                null,
                                                                editTextTextPersonName.text.toString(),
                                                                null,
                                                                null,
                                                                true
                                                            )
                                                        )
                                                    }
                                                }
                                                if  (!emailKullanimdaMi)
                                                //veritaban??nda kullanicida yok direkt kaydet
                                                {
                                                    Log.e("elsekkaydet","direktkaydetcal??st??")

                                                    Log.e("veritaban??nda kullanicida yok direkt kaydet","??al??st??")



                                                    loginroot.visibility = View.GONE
                                                    loginframe.visibility = View.VISIBLE
                                                    var transaction = supportFragmentManager.beginTransaction()
                                                    transaction.replace(
                                                        R.id.loginframe,
                                                        IsletmeKayitBilgileriFragment()
                                                    )
                                                    transaction.addToBackStack("emailileGirisFragmentEklendi")
                                                    transaction.commit()
                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            editTextTextPersonName.text.toString(),
                                                            null,
                                                            null,
                                                            true
                                                        )
                                                    )

                                                }



                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                            }

                                        })



                                    }

                                }

                            })

                    } else {
                        Toast.makeText(
                            this,
                            "L??tfen ge??erli bir E-mail  giriniz",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }

        }


    }




    override fun onBackStackChanged() {
        val elemanSayisi = mmanager.backStackEntryCount

        if (elemanSayisi == 0) {
            loginroot.visibility = View.VISIBLE
        }
    }

    fun checkMail(kontrolEdilenMail: String): Boolean {

        if (kontrolEdilenMail == null) {
            return false

        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilenMail).matches()

    }


    private fun setupAuthLis() {

        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser

                if (user!=null){
                    var intent=Intent(this@RegisterActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)


                    startActivity(intent)
                    finish()

                }else{

                }

            }

        }

    }
    override fun onStart() {
        super.onStart()
        Log.e("hata","registedas??n")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        if (mauthLis!=null){
            auth.removeAuthStateListener(mauthLis)

        }
    }
    private fun initImageLoader(){
        var universalImageLoaderr= imageLoader(this)
        ImageLoader.getInstance().init(universalImageLoaderr.config)

    }


}