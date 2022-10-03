package com.example.paylasim.profil

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.paylasim.R
import com.example.paylasim.login.RegisterActivity
import com.example.paylasim.models.kullanicilar
import com.example.paylasim.util.EventbusData
import com.example.paylasim.util.imageLoader
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profil_edit.*
import kotlinx.android.synthetic.main.fragment_profil_edit.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class profilEditFragment : Fragment() {


   lateinit var profileImage:CircleImageView
   var gelenKullaniciBilgileri=kullanicilar()

    lateinit var mDataRef:DatabaseReference
    lateinit var mStorage:StorageReference
    private lateinit var storage: FirebaseStorage



    var secilengorsel: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profil_edit, container, false)
        // Inflate the layout for this fragment

        mDataRef=FirebaseDatabase.getInstance().reference
        mStorage=FirebaseStorage.getInstance().reference
        storage=FirebaseStorage.getInstance()

        setUpKullaniciBilgileri(view)

        profileImage=view.profile_image



        setUpprofilePhoto()

        view.img_back.setOnClickListener {
            activity?.onBackPressed()

        }


        val getImage=registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {

                secilengorsel=it


                profile_image.setImageURI(secilengorsel!!)
            })

        view.foto_degis.setOnClickListener {


            getImage.launch("image/*")


            }



        view.imageView_kayit.setOnClickListener {
            if (secilengorsel!=null){
                progressBar4.visibility=View.VISIBLE


                mStorage.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!)
                    .child(secilengorsel!!.lastPathSegment!!)
                    .putFile(secilengorsel!!)
                    .addOnSuccessListener { itUploadTask ->
                        itUploadTask?.storage?.downloadUrl?.addOnSuccessListener { itUri ->
                            val downloadUrl: String = itUri.toString()
                            mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!)
                                .child("user_detail")
                                .child("profile_picture")
                                .setValue(downloadUrl).addOnCompleteListener { itTask ->
                                    if (itTask.isSuccessful) {
                                        progressBar4.visibility=View.GONE

                                        val intent=Intent(activity, profil::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        startActivity(intent)
                                        requireActivity().finish()




                                        kullaniciAdiGuncelle(view,true)
                                    } else {
                                        val message = itTask.exception?.message
                                        Toast.makeText(
                                            requireActivity(),
                                            "hata" + message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        kullaniciAdiGuncelle(view,false)

                                    }
                                }
                        }
                    }
            }else{
                
                kullaniciAdiGuncelle(view,null)


            }








        }







        return view
    }

    private fun kullaniciAdiGuncelle(view: View, profilResmiGüncellendi: Boolean?) {


        if (!gelenKullaniciBilgileri!!.user_name!!.equals(editTextTextPersonName3.text.toString())){

            if (editTextTextPersonName3.text.toString().trim().length>5){

                mDataRef.child("users").child("isletmeler").orderByChild("user_name").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var usernameKullanim=false
                        for (ds in snapshot!!.children){
                            var okunanKllaniciAdi=ds!!.getValue(kullanicilar::class.java)!!.user_name

                            if (okunanKllaniciAdi!!.equals(view.editTextTextPersonName3.text.toString())){
                                profilBilgileriGuncelle(view,profilResmiGüncellendi,false)
                                usernameKullanim=true
                                break

                            }

                        }
                        if (usernameKullanim==false){
                            mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri!!.user_id!!).child("user_name").setValue(view.editTextTextPersonName3.text.toString())
                            profilBilgileriGuncelle(view,profilResmiGüncellendi,true)




                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }else{
                Toast.makeText(activity, "Kullanıcı adı en az 6 karakter olmalıdır.",Toast.LENGTH_LONG).show()


            }



        }else{
            profilBilgileriGuncelle(view,profilResmiGüncellendi,null)


        }



    }

    private fun profilBilgileriGuncelle(view: View, profilResmiGüncellendi: Boolean?, profilBilgileriGuncellendi: Boolean?) {
        var profilGuncel:Boolean?=null

        if (!gelenKullaniciBilgileri!!.adi_soyadi!!.equals(view.editTextTextPersonName2.text.toString())){
            if (view.editTextTextPersonName2.text.toString().trim().isNotEmpty()){
                mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri!!.user_id!!).child("adi_soyadi").setValue(view.editTextTextPersonName2.text.toString())
                profilGuncel=true

            }else{
                Toast.makeText(activity, "Ad soyad boş olamaz.",Toast.LENGTH_LONG).show()

            }



        }
        if(!gelenKullaniciBilgileri!!.user_detail!!.biography.equals(view.editTextTextPersonName5.text.toString())){
            mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri!!.user_id!!).child("user_detail").child("biography").setValue(view.editTextTextPersonName5.text.toString())
            profilGuncel=true


        }

        if (profilResmiGüncellendi==null&&profilBilgileriGuncellendi==null&&profilGuncel==null){
            Toast.makeText(activity, "Lütfen Bilgileri Güncelleyiniz",Toast.LENGTH_LONG).show()
        }
        else if (profilBilgileriGuncellendi==false&&(profilResmiGüncellendi==true||profilGuncel==true)){
            Toast.makeText(activity, "Kullanıcı adı Kullanımda,diğer bilgiler güncellendi",Toast.LENGTH_LONG).show()




        }else{
            Toast.makeText(activity, "Profil Güncellendi",Toast.LENGTH_LONG).show()







        }


    }


    private fun
            setUpKullaniciBilgileri(view: View?) {
    view?.editTextTextPersonName2!!.setText(gelenKullaniciBilgileri!!.adi_soyadi)
        view?.editTextTextPersonName3.setText(gelenKullaniciBilgileri!!.user_name)
        if (!gelenKullaniciBilgileri!!.user_detail!!.biography.isNullOrEmpty()){
            view.editTextTextPersonName5.setText(gelenKullaniciBilgileri!!.user_detail!!.biography)




        }
        var imgUrl:String=gelenKullaniciBilgileri!!.user_detail!!.profile_picture!!
        imageLoader.setImage(imgUrl,view.profile_image,null,"")





    }


    private fun setUpprofilePhoto() {

        var imgUrl="www.webtekno.com/images/editor/default/0003/48/936a39992c332be0d29bc1c7d8ab580fb68ee426.jpeg"
        imageLoader.setImage(imgUrl, profileImage,null,"https://")
    }
    @Subscribe(sticky = true)

    internal fun onKullaniciBilgileriKayitEvent(kullanicibilgileri: EventbusData.kullaniciBilgileriniGonder) {
        gelenKullaniciBilgileri=kullanicibilgileri!!.kullanici!!




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