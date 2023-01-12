package com.amier.Activities.models

data class Association(
    var _id: String? = null,
    var nom: String? = null,
    var numTel: String? = null,
    var photo: String? = null,
    var categorie: String? = null,
    var user: String? = null,
    var __v: Int? = null,
    var association: MutableList<Association>? = null,
    var articles: MutableList<String>? = null,
    var article: String? = null,
)
