package com.example.paylasim.models

class kullaniciKampanya {
    var userID:String?=null
    var userName:String?=null
    var userPhotoURL:String?=null
    var postID:String?=null
    var postAciklama:String?=null
    var geri_sayim:String?=null
    var postURL:String?=null
    var postYuklenmeTarih:Long?=null

    constructor(userID: String?, userName: String?, userPhotoURL: String?, postID: String?, postAciklama: String?,geri_sayim:String?, postURL: String?, postYuklenmeTarih: Long?) {
        this.userID = userID
        this.userName = userName
        this.userPhotoURL = userPhotoURL
        this.postID = postID
        this.postAciklama = postAciklama
        this.geri_sayim=geri_sayim
        this.postURL = postURL
        this.postYuklenmeTarih = postYuklenmeTarih
    }

    constructor(){}

    override fun toString(): String {
        return "UserPosts(userID=$userID, userName=$userName, userPhotoURL=$userPhotoURL, postID=$postID, postAciklama=$postAciklama,,geri_sayim=$geri_sayim postURL=$postURL, postYuklenmeTarih=$postYuklenmeTarih)"
    }

}