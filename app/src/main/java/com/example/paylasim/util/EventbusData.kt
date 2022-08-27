package com.example.paylasim.util

import com.example.paylasim.models.kullanicilar

class EventbusData {
    internal class kayitBilgileriniGonder(var telNo:String?, var email:String?, var verificationID:String?, var code:String?, var emailkayit:Boolean)

    internal class kullaniciBilgileriniGonder(var kullanici:kullanicilar?)

    internal class YorumYapilacakGonderininIDsiniGonder(var gonderiID:String?)
}