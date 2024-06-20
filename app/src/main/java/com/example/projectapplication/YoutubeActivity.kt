package com.example.projectapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projectapplication.databinding.ActivityYoutubeBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import java.util.Random

class YoutubeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)
        val binding = ActivityYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)

                val videoId :String
                videoId= "k3K1AQlUkxk"
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
        lifecycle.addObserver(binding.youtubePlayerView2)
        binding.youtubePlayerView2.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)

                val videoId :String
                videoId= "Qh3C6rH36Nw"
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
        lifecycle.addObserver(binding.youtubePlayerView3)
        binding.youtubePlayerView3.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)

                val videoId :String
                videoId= "aekLkvAOyX0"
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
        lifecycle.addObserver(binding.youtubePlayerView4)
        binding.youtubePlayerView4.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)

                val videoId :String
                videoId= "ay48IGuoOmA"
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })

    }
    override fun onResume() {
        super.onResume()
        applyUserSettings()
    }
}