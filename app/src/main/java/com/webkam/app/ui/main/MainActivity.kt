package com.webkam.app.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.webkam.app.R
import com.webkam.app.databinding.ActivityMainBinding
import com.webkam.app.ui.camera.CameraActivity
import com.webkam.app.ui.market.MarketFragment
import com.webkam.app.ui.settings.SettingsFragment
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        val lang = PreferenceManager.getLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Load market fragment by default
        if (savedInstanceState == null) {
            loadFragment(MarketFragment())
        }

        setupBottomNavigation()
    }

    private fun applyTheme() {
        val theme = PreferenceManager.getTheme(this)
        if (theme == com.webkam.app.data.model.ThemeMode.DARK) {
            setTheme(R.style.Theme_Bakam_Dark)
        } else {
            setTheme(R.style.Theme_Bakam)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(MarketFragment())
                    true
                }
                R.id.nav_camera -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun refreshActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}