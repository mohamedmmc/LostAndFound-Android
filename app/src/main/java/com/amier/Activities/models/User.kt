package com.amier.Activities.models

data class User(
    var _id: String? = null,
    var email: String? = null,
    var nom: String? = null,
    var password: String? = null,
    var prenom: String? = null,
    var photoProfil: String? = null,
    var numt: String? = null,
    val user : User? = null,
    var token : String? = null,
    var tokenfb : String? = null,
    val reponse : String? = null,
    var code: Int?= null,
    val message:String? = null,
    val isVerified:Boolean? = null,
    var type:String? = null


)
