package com.example.projectapplication

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.projectapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var animation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the animation drawable as the background of the ImageView
        binding.splash.setBackgroundResource(R.drawable.greenbuddy)
        animation = binding.splash.background as AnimationDrawable
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            animation.start()

            // Handler to wait until the animation completes then start the main activity
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, (animation.numberOfFrames * 400).toLong())  // Calculate the total duration based on frame count and duration
        }
    }
}
