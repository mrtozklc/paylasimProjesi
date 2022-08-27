package com.example.paylasim.models

class mesaj {
     var mesaj:String?=null
     var gonderilmeZamani:Long?=null
     var type:String?=null
     var goruldu:Boolean?=null
     var user_id:String?=null

    constructor()
    constructor(
        mesaj: String?,
        gonderilmeZamani: Long?,
        type: String?,
        goruldu: Boolean?,
        user_id: String?
    ) {
        this.mesaj = mesaj
        this.gonderilmeZamani = gonderilmeZamani
        this.type = type
        this.goruldu = goruldu
        this.user_id = user_id
    }

     override fun toString(): String {
        return "mesaj(mesaj=$mesaj, gonderilmeZamani=$gonderilmeZamani, type=$type, goruldu=$goruldu, user_id=$user_id)"
    }


}