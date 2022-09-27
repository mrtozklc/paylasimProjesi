package com.example.paylasim.models

class kullaniciDetaylari {
    var follower:String? = null
    var following:String? = null
    var post:String?=null
    var profile_picture:String? = null
    var biography:String? = null
    var web_site:String? = null
    var adress:String?=null
    var latitude: Double? = 0.0
    var longitude: Double? = 0.0

    constructor(){}

    constructor(follower: String?, following: String?, post: String?, profile_picture: String?, biography: String?, web_site: String?,adress:String?,latitude:Double?,longitude:Double?) {
        this.follower = follower
        this.following = following
        this.post = post
        this.profile_picture = profile_picture
        this.biography = biography
        this.web_site = web_site
        this.adress=adress
        this.latitude=latitude
        this.longitude=longitude

    }

    override fun toString(): String {
        return "UserDetails(follower=$follower, following=$following, post=$post, profile_picture=$profile_picture, biography=$biography, web_site=$web_site,adress=$adress,latitude=$latitude,longutide=$longitude)"
    }
}