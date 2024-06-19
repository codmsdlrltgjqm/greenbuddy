package com.example.projectapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BoardViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

class BoardAdapter (val context: Context, val itemList: MutableList<ItemData>): RecyclerView.Adapter<BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BoardViewHolder(ItemCommentBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val data = itemList.get(position)

        // 날짜 형식 변환을 위한 포맷 설정
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // data.date_time을 Date 객체로 파싱하여 원하는 포맷으로 변환
        val date = inputFormat.parse(data.date_time)
        val formattedDate = outputFormat.format(date)

        // 변환된 날짜를 TextView에 설정
        holder.binding.dateTextView.text = formattedDate

        var imageUrl: String? = null
        val imageRef = MyApplication.storage.reference.child("image/${data.docId}.jpg")
        imageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageUrl = task.result.toString()
                holder.binding.itemImageView.visibility = View.VISIBLE
                Glide.with(context)
                    .load(task.result)
                    .centerCrop() // 이미지를 ImageView에 꽉 채우도록 설정
                    .error(R.drawable.greenbuddy1)
                    .into(holder.binding.itemImageView)
            } else {
                // 이미지 로드 실패 시 기본 이미지를 보여줄 수 있도록 처리
                holder.binding.itemImageView.setImageResource(R.drawable.greenbuddy1)
            }

            // 이미지뷰 클릭 리스너
            holder.binding.itemImageView.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java).apply {
                    // 클릭된 아이템의 데이터를 DetailActivity로 전달
                    putExtra("docId", data.docId)
                    putExtra("title", data.title)
                    putExtra("stars", data.stars)
                    putExtra("comments", data.comments)
                    putExtra("date_time", data.date_time)
                    putExtra("imageUrl", imageUrl) // 이미지 URL이 null일 수 있으므로 주의
                }
                context.startActivity(intent)
            }
        }
    }

}


