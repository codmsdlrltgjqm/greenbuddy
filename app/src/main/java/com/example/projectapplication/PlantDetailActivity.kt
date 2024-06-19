package com.example.projectapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.projectapplication.databinding.ActivityPlantDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityPlantDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 데이터 가져오기
        val cntntsNo = intent.getStringExtra("cntntsNo")?.toIntOrNull()
        val mainImageUrl = intent.getStringExtra("rtnFileUrl")

        if (cntntsNo != null) {
            fetchPlantDetails(cntntsNo)
        } else {
            Log.e("PlantDetailActivity", "Invalid cntntsNo")
        }

        // 데이터가 유효한지 확인하고 UI에 설정
        binding.tvPlantName.text = cntntsNo.toString()

        Glide.with(this)
            .load(mainImageUrl)
            .override(400, 300)
            .into(binding.ivPlantImage)
    }

    private fun fetchPlantDetails(cntntsNo: Int) {
        val call = RetrofitConnection.xmlNetworkService.getXmlDetailList(
            apiKey = "202406174DFRSMNFEBARLJHHDHLAHW",
            cntntsNo = cntntsNo,
            pageNo = 1,
            numOfRows = 1,
            returnType = "xml"
        )

        call.enqueue(object : Callback<XmlDetailResponse> {
            override fun onResponse(call: Call<XmlDetailResponse>, response: Response<XmlDetailResponse>) {
                if (response.isSuccessful) {
                    val detailItem = response.body()?.body?.item
                    if (detailItem != null) {
                        updateUI(detailItem)
                    } else {
                        Log.e("PlantDetailActivity", "No detail item found")
                    }
                } else {
                    Log.e("PlantDetailActivity", "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<XmlDetailResponse>, t: Throwable) {
                Log.e("PlantDetailActivity", "Network request failed", t)
            }
        })
    }

    private fun updateUI(item: myXmlDetailItem) {
        binding.tvPlantName.text = item.plntbneNm ?: "정보 없음"
        binding.tvPlntzrName.text = item.plntzrNm ?: "정보 없음"
        binding.tvAdviseInfo.text = item.adviseInfo ?: "정보 없음"
        binding.tvSoilInfo.text = item.soilInfo ?: "정보 없음"
        binding.tvHdCodeNm.text = item.hdCodeNm ?: "정보 없음"
        binding.tvFrtlzrInfo.text = item.frtlzrInfo ?: "정보 없음"
        binding.tvPrpgtEraInfo.text = item.prpgtEraInfo
        binding.tvWaterCycleSpring.text = item.watercycleSprngCodeNm ?: "정보 없음"
        binding.tvWaterCycleSummer.text = item.watercycleSummerCodeNm ?: "정보 없음"
        binding.tvWaterCycleAutumn.text = item.watercycleAutumnCodeNm ?: "정보 없음"
        binding.tvWaterCycleWinter.text = item.watercycleWinterCodeNm ?: "정보 없음"
    }
    override fun onResume() {
        super.onResume()
        applyUserSettings()
    }
}
