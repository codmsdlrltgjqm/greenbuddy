// XmlDetailAdapter.kt

package com.example.projectapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ActivityPlantDetailBinding

class XmlDetailViewHolder(val binding: ActivityPlantDetailBinding) : RecyclerView.ViewHolder(binding.root)

class XmlDetailAdapter(private val context: Context, private val items: List<myXmlDetailItem>) : RecyclerView.Adapter<XmlDetailViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): XmlDetailViewHolder {
        val binding = ActivityPlantDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return XmlDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: XmlDetailViewHolder, position: Int) {
        val binding = holder.binding
        val item = items[position]

        binding.tvPlantName.text = item.plntbneNm ?: "정보 없음"
        binding.tvAdviseInfo.text = item.adviseInfo ?: "정보 없음"

        binding.root.setOnClickListener {
            val intent = Intent(context, PlantDetailActivity::class.java).apply {
                putExtra("EXTRA_PLANT_ITEM", item)
            }
            context.startActivity(intent)
        }
    }
}
