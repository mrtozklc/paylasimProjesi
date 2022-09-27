package com.example.paylasim.profil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.paylasim.R
import com.example.paylasim.home.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profil_ayarlar.*
import kotlinx.android.synthetic.main.fragment_sifre_degistir.view.*


class sifreDegistirFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_sifre_degistir, container, false)


        view.sifre_back.setOnClickListener{
            requireActivity().onBackPressed()
        }


        view.sifre_onay.setOnClickListener {

            var mevcutSifre=view.mevcutSifre_id.text.toString()
            var yeniSifre=view.yeniSifre_id.text.toString()
            var yeniSifreTekrar=view.tekrarSifre_id.text.toString()


            if (mevcutSifre.length>=6 && mevcutSifre.isNotEmpty()){

                var auth=FirebaseAuth.getInstance().currentUser

                if (auth!=null){
                    var credential=EmailAuthProvider.getCredential(auth.email.toString(),mevcutSifre)
                    auth.reauthenticate(credential).addOnCompleteListener(object :OnCompleteListener<Void>{
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful){

                                if (yeniSifre.equals(yeniSifreTekrar)){
                                    if (yeniSifre.length>=6&&yeniSifre.isNotEmpty()){
                                        if (!mevcutSifre.equals(yeniSifre)){
                                            auth.updatePassword(yeniSifre).addOnCompleteListener(object :OnCompleteListener<Void>{
                                                override fun onComplete(p0: Task<Void>) {
                                                    if (p0.isSuccessful){
                                                        Toast.makeText(requireActivity(),"Şifre güncellendi.",Toast.LENGTH_LONG).show()
                                                        val intent=Intent(activity!!,MainActivity::class.java)
                                                        startActivity(intent)
                                                       activity!!.finish()





                                                    }else{
                                                        Toast.makeText(requireActivity(),"Şifre güncellenemedi,Lütfen daha sonra tekrar deneyin.",Toast.LENGTH_LONG).show()


                                                    }
                                                }

                                            })

                                        }else{
                                            Toast.makeText(requireActivity(),"Yeni şifre eski şifreyle aynı olamaz.",Toast.LENGTH_LONG).show()


                                        }



                                    }else{
                                        Toast.makeText(requireActivity(),"Yeni şifre en az 6 karakter olmalıdır.",Toast.LENGTH_LONG).show()


                                    }

                                }else{

                                    Toast.makeText(requireActivity(),"şifreler eşleşmiyor.",Toast.LENGTH_LONG).show()


                                }

                            }else{
                                Toast.makeText(requireActivity(),"Mevcut şifre yanlış.",Toast.LENGTH_LONG).show()


                            }
                        }

                    })
                }




            }else{
                Toast.makeText(requireActivity(),"Mevcut şifre en az 6 karakter olmalıdır.",Toast.LENGTH_LONG).show()
            }
        }




        return view

    }


}