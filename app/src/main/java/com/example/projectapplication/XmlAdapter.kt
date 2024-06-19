package com.example.projectapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ItemMainBinding
import com.example.projectapplication.myXmlItem
import java.io.Serializable

class XmlViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class XmlAdapter(val datas: MutableList<myXmlItem>, val context: Context) : RecyclerView.Adapter<XmlAdapter.XmlViewHolder>() {

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XmlViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return XmlViewHolder(binding)
    }

    override fun onBindViewHolder(holder: XmlViewHolder, position: Int) {
        val binding = holder.binding
        val model = datas[position]

        binding.cntntsSj.text = model.cntntsSj
        binding.plantId.text = model.cntntsNo

        // 이미지 로드
        val fileUrls = model.rtnFileUrl?.split("|")
        val mainImageUrl = fileUrls?.getOrNull(0)

        Glide.with(context)
            .load(mainImageUrl)
            .override(400, 300)
            .into(binding.rtnFileUrl)

        // 아이템 클릭 시 상세 페이지로 이동
        binding.rtnFileUrl.setOnClickListener {
            val intent = Intent(context, PlantDetailActivity::class.java).apply{
                putExtra("cntntsNo", model.cntntsNo)
                putExtra("rtnFileUrl", mainImageUrl)

            }
            context.startActivity(intent)
        }
    }

    inner class XmlViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)
}

