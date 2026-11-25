package com.webkam.app.ui.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.webkam.app.databinding.ActivityLanguageBinding
import com.webkam.app.ui.main.MainActivity
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager

class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnArabic.setOnClickListener {
            selectLanguage("ar")
        }
        binding.btnEnglish.setOnClickListener {
            selectLanguage("en")
        }
    }

    private fun selectLanguage(lang: String) {
        PreferenceManager.setLanguage(this, lang)
        PreferenceManager.setFirstLaunchDone(this)

        // Apply locale and restart
        LocaleHelper.setLocale(this, lang)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
