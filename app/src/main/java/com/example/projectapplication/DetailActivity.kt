package com.example.projectapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.projectapplication.MyApplication.Companion.email
import com.example.projectapplication.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 데이터 받기
        val title = intent.getStringExtra("title")
        val stars = intent.getFloatExtra("stars", 0.0f)
        val comments = intent.getStringExtra("comments")
        val dateTime = intent.getStringExtra("date_time")
        val imageUrl = intent.getStringExtra("imageUrl")

        // 받은 데이터를 화면에 표시
        binding.titleTextView.text = title
        binding.ratingBar.rating = stars
        binding.contentsTextView.text = comments
        binding.dateTextView.text = dateTime

        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .error(R.drawable.greenbuddy1) // 이미지 로드 실패 시 보여줄 기본 이미지 설정
                .into(binding.itemImageView)
        } else {
            // imageUrl이 null이거나 empty인 경우 기본 이미지 설정
            binding.itemImageView.setImageResource(R.drawable.greenbuddy1)
        }
    }
    override fun onResume() {
        super.onResume()
        applyUserSettings()
    }
}