package com.example.paylasim.models

class kullaniciKonumlari {


    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    var konumkullaniciId: String? = null

    constructor(latitude:Double?,longitude:Double?,konumkullaniciId:String?) {

        this.latitude=latitude
        this.longitude=longitude
        this.konumkullaniciId= konumkullaniciId
    }

    constructor(){}

    override fun toString(): String {
        return "latitude=$latitude,longitude=$longitude)"
    }
}