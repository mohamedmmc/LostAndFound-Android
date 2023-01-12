package com.amier.Activities.models

data class Report(
    var _id: String? = null,
    var article: String? = null,
    var userr: MutableList<String>? = ArrayList(),
    var user: String? = null,
)
