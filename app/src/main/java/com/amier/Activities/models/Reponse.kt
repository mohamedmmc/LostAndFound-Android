package com.amier.Activities.models

data class Reponse(

    var _id: String? = null,
    var description: String? = null,
    var userr: String? = null,
    var user: User? = null,
    var _v: Int? = null,
    var reponses: MutableList<Reponse>? = null
)
