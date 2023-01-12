package com.amier.Activities.models

data class SendBirdUser(
    var user_id: String? = null,
    var nickname: String? = null,
    var profile_url: String? = null,
    var has_ever_logged_in: Boolean? = null,
    var require_auth_for_profile_image: Boolean? = null,
    var is_active: Boolean? = null,
    var phone_number: Boolean? = null,
    var is_created: Boolean? = null,
    var last_seen_at: Int? = null,
    var is_online: Boolean? = null,
    var access_token: String? = null,
    var session_tokens: MutableList<String>? = null,
    var discovery_keys: MutableList<String>? = null,

)
