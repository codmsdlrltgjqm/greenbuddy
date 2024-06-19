package com.example.projectapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ActivityAddBinding
import java.text.SimpleDateFormat

class AddActivity : BaseActivity() {
    lateinit var binding: ActivityAddBinding
    lateinit var uri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvId.text = MyApplication.email
        //화면에 보여주기 위한 작업
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode === android.app.Activity.RESULT_OK){
                binding.addImageView.visibility = View.VISIBLE
                Glide
                    .with(applicationContext) //load가 인식이 잘 안 된다면
                    .load(it.data?.data) // 이미지 가져오삼
                    .override(200,150) //크기조정
                    .into(binding.addImageView) // 여기에 이미지 넣으삼
                uri = it.data?.data!!
                val selectedImageUri = it.data?.data
                Log.d("Debug", "Selected Image URI: $selectedImageUri")

            }
        }
        binding.uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }
        binding.saveButton.setOnClickListener{
            if(binding.input.text.isNotEmpty()){
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                var data = mapOf(
                    "email" to MyApplication.email,
                    "title" to binding.tvTitle.text.toString(),
                    "stars" to binding.ratingBar.rating.toFloat(),
                    "comments" to binding.input.text.toString(),
                    "date_time" to dateFormat.format(System.currentTimeMillis())
                )
                MyApplication.db.collection("comments")
                    .add(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "데이터 저장 성공", Toast.LENGTH_LONG).show()
                        uploadImage(it.id)
                        finish()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "데이터 저장 실패", Toast.LENGTH_LONG).show()
                    }
            }
            else{
                Toast.makeText(this, "한줄평을 먼저 입력해주세요..", Toast.LENGTH_LONG).show()
            }
        }


    }
    fun uploadImage(docId : String){
        val imageRef = MyApplication.storage.reference.child("image/${docId}.jpg")

        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "사진 업로드 성공", Toast.LENGTH_LONG).show()
        }
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_LONG).show()
        }
    }
    override fun onResume() {
        super.onResume()
        applyUserSettings()
    }
}