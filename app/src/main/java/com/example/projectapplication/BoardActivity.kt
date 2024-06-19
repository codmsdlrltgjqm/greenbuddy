package com.example.projectapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectapplication.databinding.ActivityBoardBinding
import com.google.firebase.firestore.Query

class BoardActivity : BaseActivity() {
    lateinit var binding: ActivityBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.mainFab.setOnClickListener{
            if(MyApplication.checkAuth()){
                startActivity(Intent(this, AddActivity::class.java))
            }
            else{
                Toast.makeText(this, "인증을 먼저 진행해주세요..", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (MyApplication.checkAuth()) {
            MyApplication.db.collection("comments")
                .orderBy("date_time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val itemList = mutableListOf<ItemData>()
                    // Populate itemList from Firestore
                    for(document in result){
                        val item = document.toObject(ItemData::class.java)
                        item.docId = document.id
                        itemList.add(item)
                    }
                    // Use GridLayoutManager to display 3 items per row
                    val layoutManager = GridLayoutManager(this, 3)
                    binding.recyclerView.layoutManager = layoutManager
                    binding.recyclerView.adapter = BoardAdapter(this, itemList)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "서버 데이터 획득 실패.", Toast.LENGTH_LONG).show()
                }
        }
    }
    override fun onResume() {
        super.onResume()
        applyUserSettings()
    }
}