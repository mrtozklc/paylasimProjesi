package com.example.paylasim.models

class sabitKampanya {
    var user_id:String? = null
    var post_id:String? = null
    var yuklenme_tarih:Long? = null
    var aciklama:String? = null
    var file_url:String? = null

    constructor(){}
    constructor(user_id: String?, post_id: String?, yuklenme_tarih: Long?, aciklama: String?, photo_url: String?) {
        this.user_id = user_id
        this.post_id = post_id
        this.yuklenme_tarih = yuklenme_tarih
        this.aciklama = aciklama
        this.file_url = photo_url
    }

    override fun toString(): String {
        return "kampanya(user_id=$user_id, post_id=$post_id, yuklenme_tarih=$yuklenme_tarih, aciklama=$aciklama,file_url=$file_url)"
    }
}