package com.example.paylasim.login

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.paylasim.R
import com.google.firebase.auth.FirebaseAuth

class signOutFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var alert = AlertDialog.Builder(this!!.requireActivity(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
            .setTitle("Çıkış Yap")
            .setMessage("Emin misiniz?")
            .setPositiveButton("Çıkış Yap", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                    FirebaseAuth.getInstance().signOut()
                    activity!!.finish()

                }

            })
            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                }

            })
            .create()

        return alert
    }
}