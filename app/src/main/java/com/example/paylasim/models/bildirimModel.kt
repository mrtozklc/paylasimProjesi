package com.example.paylasim.models

class bildirimModel {
    var bildirim_tur:Int?=null
    var time:Long?=null
    var user_id:String?=null
    var gonderi_id:String?=null

    constructor(){}



    constructor(bildirim_tur: Int?, time: Long?, user_id: String?, gonderi_id: String?) {
        this.bildirim_tur = bildirim_tur
        this.time = time
        this.user_id = user_id
        this.gonderi_id = gonderi_id
    }

    constructor(bildirim_tur: Int?, time: Long?, user_id: String?) {
        this.bildirim_tur = bildirim_tur
        this.time = time
        this.user_id = user_id
    }






    override fun toString(): String {
        return "BildirimModel(bildirim_tur=$bildirim_tur, time=$time, user_id=$user_id)"
    }
}