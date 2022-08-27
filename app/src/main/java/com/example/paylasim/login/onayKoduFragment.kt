package com.example.paylasim.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.paylasim.util.EventbusData
import com.example.paylasim.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_onay_kodu.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit


class onayKoduFragment : Fragment() {
    var gelenTelefon=""
    var verificationID=""
    var GelenCode =""
    var gelenEmail=""
    private lateinit var auth: FirebaseAuth
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var progressBar: ProgressBar



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_onay_kodu, container, false)

        view.tw_kullanicitelno.setText(gelenTelefon)
        progressBar=view.progressBar_onaykodu
        setupCallback()

        auth= FirebaseAuth.getInstance()

        view.btn_devamet.setOnClickListener {

            if(GelenCode.equals(view.editText_onayKodu.text.toString().trim())){
                EventBus.getDefault().postSticky(EventbusData.kayitBilgileriniGonder(gelenTelefon,null, verificationID,GelenCode,false))
                var transaction=activity?.supportFragmentManager!!.beginTransaction()
                transaction.replace(R.id.loginframe,emailOnayFragment())
                transaction.addToBackStack("kayitFragmentEklendi")
                transaction.commit()
            }else {
                Toast.makeText(activity,"Kod Hatalı",Toast.LENGTH_SHORT).show()
            }

        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+9"+gelenTelefon)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this.requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        return  view
    }


    private fun setupCallback() {
        progressBar.visibility=View.VISIBLE
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if(!credential.smsCode.isNullOrEmpty())
                {
                    GelenCode = credential.smsCode!!
                    progressBar.visibility=View.INVISIBLE
                    //Log.e("HATA","on verification completed sms gelmiş:"+ gelenKod)

                }else{
                    //Log.e("HATA","on verification completed sms gelmeyecek")
                }


            }

            override fun onVerificationFailed(e: FirebaseException) {
                //Log.e("HATA","Hata çıktı: "+e.message)
                Toast.makeText(activity,"hata:"+e.message,Toast.LENGTH_SHORT).show()
                progressBar.visibility=View.INVISIBLE
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                verificationID =p0
                progressBar.visibility=View.VISIBLE
                //Log.e("HATA","oncodesent çalıştı")
            }
        }
    }


    @Subscribe(sticky = true)

   internal fun onayKoduEvent(kayitBilgileri: EventbusData.kayitBilgileriniGonder){
        gelenTelefon= kayitBilgileri.telNo!!.toString()


        Log.e("murat","telefon"+"+9"+gelenTelefon)

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