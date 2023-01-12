package com.amier.Activities.storage

import android.content.Context
import com.amier.Activities.models.User

class SharedPrefManager private constructor(private val mCtx: Context){
    val isLoggedIn: Boolean
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("_id", null ) != null
        }

    val user: User
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                sharedPreferences.getString("_id", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("nom", null),
                sharedPreferences.getString("prenom", null),
                sharedPreferences.getString("photoProfil", null),
                sharedPreferences.getString("tokenfb", null)
            )
        }


    fun saveUser(user: User) {

        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("_id", user._id)
        editor.putString("email", user.email)
        editor.putString("nom", user.nom)
        editor.putString("prenom", user.prenom)
        editor.putString("photoProfil", user.photoProfil)
        editor.putString("tokenfb", user.tokenfb)

        editor.apply()

    }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "my_shared_preff"
        private var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }
}