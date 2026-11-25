package com.webkam.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.webkam.app.databinding.ActivitySplashBinding
import com.webkam.app.ui.language.LanguageActivity
import com.webkam.app.ui.main.MainActivity
import com.webkam.app.utils.PreferenceManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Animate HELLO text
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeIn.duration = 1200
        binding.tvHello.startAnimation(fadeIn)

        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideUp.duration = 900
        binding.tvProgrammer.startAnimation(slideUp)

        // Navigate after 2.5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val isFirstLaunch = PreferenceManager.isFirstLaunch(this)
            if (isFirstLaunch) {
                startActivity(Intent(this, LanguageActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2500)
    }
}
