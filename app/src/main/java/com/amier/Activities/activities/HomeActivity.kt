package com.amier.Activities.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.amier.Activities.storage.SharedPrefManager
import com.amier.modernloginregister.R
import com.amier.modernloginregister.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private var fragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpBar()


    }
    private fun setUpBar() {

        binding.bottomNavBar.setOnItemSelectedListener {
            when(it) {
                R.id.nav_home -> {
                    fragment = HomeFragment()
                }
                R.id.nav_location -> {
                    fragment = LocationFragment()
                }
                R.id.nav_search -> {
                    fragment = SearchFragment()
                }
                R.id.nav_last -> {
                    fragment = LastFragment()
                }

            }

            fragment!!.let {
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment!!)
                    .addToBackStack(null)
                    .commit()
            }
        }


    }


}