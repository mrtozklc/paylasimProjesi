package com.example.paylasim.models

class konumlar  {

var isletmeLatitude: Double? = 0.0
var isletmeLongitude: Double? = 0.0
var kullaniciLatitude: Double? = 0.0
var kullaniciLongitude: Double? = 0.0

constructor(isletmeLatitude:Double?,isletmeLongitude:Double?,kullaniciLatitude:Double?,kullaniciLongitude:Double?) {

 this.isletmeLatitude=isletmeLatitude
 this.isletmeLongitude=isletmeLongitude
 this.kullaniciLatitude=kullaniciLatitude
 this.kullaniciLongitude=kullaniciLongitude
}

constructor(){}

override fun toString(): String {
 return "isletmeLatitude=$isletmeLatitude,isletmeLongitude=$isletmeLongitude,kullanicilatitude=$kullaniciLatitude,kullaniciLongitude=$kullaniciLongitude)"
}
}



