package com.example.paylasim.models

class konusmalar {
    var goruldu:Boolean? = null
    var son_mesaj:String? = null
    var gonderilmeZamani:Long? = null
    var user_id:String? = null

    constructor()
    constructor(goruldu: Boolean?, son_mesaj: String?, time: Long?) {
        this.goruldu = goruldu
        this.son_mesaj = son_mesaj
        this.gonderilmeZamani = time
    }
    constructor(goruldu: Boolean?, son_mesaj: String?, time: Long?, user_id: String?) {
        this.goruldu = goruldu
        this.son_mesaj = son_mesaj
        this.gonderilmeZamani = time
        this.user_id = user_id
    }

    override fun toString(): String {
        return "konusmalar(goruldu=$goruldu, son_mesaj=$son_mesaj, gonderilmeZamani=$gonderilmeZamani, user_id=$user_id)"
    }

}