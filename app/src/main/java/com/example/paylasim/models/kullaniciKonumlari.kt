package com.example.paylasim.models

class kullaniciKonumlari {


    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

    constructor(latitude:Double?,longitude:Double?) {

        this.latitude=latitude
        this.longitude=longitude
    }

    constructor(){}

    override fun toString(): String {
        return "latitude=$latitude,longitude=$longitude)"
    }
}