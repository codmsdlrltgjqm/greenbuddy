package com.example.projectapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.LayoutInflaterCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectapplication.databinding.ActivityMainBinding
import com.example.projectapplication.databinding.NavigationHeaderBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var isLoading = false
    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: NavigationHeaderBinding
    private val items = mutableListOf<myXmlItem>()
    private lateinit var xmlAdapter: XmlAdapter
    private lateinit var headerview: View
    private lateinit var toggle: ActionBarDrawerToggle
    //private lateinit var sharedPreference: SharedPreferences
    private lateinit var permissionLauncher:ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        // 색상 설정 반영
        applyBackgroundColor()

        applyUserSettings() // 사용자 설정 적용

        applyUserPhoto()
        // 텍스트 크기 설정 반영
        applyTextSize()

        // 알람 notification
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions() ) {
            if (it.all { permission -> permission.value == true }) { // 퍼미션 허용으로 변경
                showNotification()
            }
            else {
                Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
        // DrawerLayout Toggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawer,
            R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        // Drawer 메뉴
        binding.mainDrawerView.setNavigationItemSelectedListener(this)
        headerview = binding.mainDrawerView.getHeaderView(0) // 하나밖에 없으니까 0
        val button = headerview.findViewById<Button>(R.id.btnAuth)

        button.setOnClickListener {
            Log.d("mobileApp", "button.setOnClickListener")
            val intent = Intent(this, AuthActivity::class.java)
            if (button.text == "로그인") {
                intent.putExtra("status", "logout")
            } else if (button.text == "로그아웃") {
                intent.putExtra("status", "login")
            }
            startActivity(intent)
            binding.drawer.closeDrawers()
        }

        // 어댑터 설정
        xmlAdapter = XmlAdapter(items, this)

        binding.xmlRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.xmlRecyclerView.adapter = xmlAdapter
        binding.xmlRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        // 검색 버튼 클릭 리스너 설정
        binding.btnSearch.setOnClickListener {
            if (MyApplication.checkAuth()) {
                items.clear() // 기존 데이터 초기화
                xmlAdapter.notifyDataSetChanged() // 어댑터 갱신
                loadData() // 첫 페이지 데이터 요청

                // 현재 시간 저장
                saveCurrentTime()
            } else {
                Toast.makeText(this, "인증을 먼저 진행해주세요.", Toast.LENGTH_LONG).show()
            }
        }

        // UI에 마지막 검색 시간 표시
        displayLastSearchTime()
    }

    private fun loadData() {
        if (!isLoading) {
            isLoading = true

            val sText = binding.edtName.text.toString()
            val call: Call<XmlResponse> = RetrofitConnection.xmlNetworkService.getXmlList(
                "202406174DFRSMNFEBARLJHHDHLAHW",
                sText,
                1, // 첫 페이지 데이터 요청
                1000, // 충분히 큰 값으로 설정하여 모든 데이터 불러오기
                "xml"
            )

            call.enqueue(object : Callback<XmlResponse> {
                override fun onResponse(call: Call<XmlResponse>, response: Response<XmlResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.body.items.item.isNotEmpty()) {
                            val allItems = responseBody.body.items.item

                            // 검색어와 정확히 일치하는 항목을 상단에 배치
                            val exactMatches = allItems.filter {
                                it.cntntsSj.equals(sText, ignoreCase = true)
                            }

                            // 나머지 항목을 추가
                            val otherItems = allItems.filter {
                                !it.cntntsSj.equals(sText, ignoreCase = true)
                            }

                            // 리스트 업데이트
                            items.clear()
                            items.addAll(exactMatches)
                            items.addAll(otherItems)
                            xmlAdapter.notifyDataSetChanged()
                            saveCurrentTime()  // 검색 완료 후 현재 시간 저장
                        } else {
                            Log.d("mobileApp", "Response Body is null or empty")
                        }
                    } else {
                        Log.d("mobileApp", "Response not successful: ${response.errorBody()}")
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<XmlResponse>, t: Throwable) {
                    Log.d("mobileApp", "onFailure ${call.request()}")
                    t.printStackTrace()
                    isLoading = false
                }
            })
        }

}

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_board -> {
                Log.d("mobileapp", "게시판 메뉴")
                val intent = Intent(this, BoardActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                return true
            }
            R.id.item_setting -> {
                Log.d("mobileapp", "설정 메뉴")
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                return true
            }
            R.id.item_youtube -> {
                Log.d("mobileapp", "영상 보러가기 메뉴")
                val intent = Intent(this, YoutubeActivity::class.java)
                startActivity(intent)
                binding.drawer.closeDrawers()
                return true
            }

            R.id.item_notification -> {
                Log.d("mobileapp", "그린버디 알림 메뉴")
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 티라미수버전보다 높으면
                    // 이 앱이 POST_NOTIFICATIONS 퍼미션을 획득했는지 확인
                    if (ContextCompat.checkSelfPermission(this,"android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
                        showNotification()
                    } // POST_NOTIFICATIONS 퍼미션 없으면
                    else {
                        //사용자에게 POST_NOTIFICATIONS 퍼미션 허용해줘 물어보는 거, 위에 permissionLauncher 호출
                        permissionLauncher.launch( arrayOf( "android.permission.POST_NOTIFICATIONS"  ) )
                    }
                }
                else {
                    showNotification()
                }
                binding.drawer.closeDrawers()
                return true
            }
        }
        return false
    }

    // 알림 표시 메소드
    private fun showNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "one-channel"
            val channelName = "My Channel One"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "My Channel One Description"
                setShowBadge(true)
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(uri, audioAttributes)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(this, channelId)
        } else {
            builder = NotificationCompat.Builder(this)
        }

        builder.run {
            setSmallIcon(R.drawable.bud)
            setWhen(System.currentTimeMillis())
            setContentTitle("그린버디 🌱")
            setContentText("안녕하세요. 그린버디에서 다양한 식물들이 ${MyApplication.email}님을 기다리고 있답니다!")
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.greenbuddy1))
        }

        manager.notify(11, builder.build())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        // 로그인 상태에 따라 버튼 텍스트 변경
        val button = headerview.findViewById<Button>(R.id.btnAuth)
        val tvID = headerview.findViewById<TextView>(R.id.tvID)

        if (MyApplication.checkAuth()) {
            MyApplication.auth.currentUser?.let { user ->
                // 사용자가 로그인한 경우
                val email = user.email

                // Firestore에서 사용자 데이터 문서 가져오기
                MyApplication.db.collection("users").document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Firestore에 사용자 데이터가 있는 경우
                            val name = document.getString("name")
                            if (name != null) {
                                // 이름이 있을 경우 처리
                                button.text = "로그아웃"
                                tvID.text = "$name 님\n반갑습니다"
                            } else {
                                // 이름이 Firestore에 저장되지 않은 경우
                                button.text = "로그아웃"
                                tvID.text = "사용자 이름이 없습니다."
                            }
                        } else {
                            // Firestore에 사용자 데이터가 없는 경우
                            button.text = "로그아웃"
                            if (email != null) {
                                tvID.text = "$email 님\n반갑습니다"
                            } else {
                                tvID.text = "이메일이 없습니다."
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Firestore에서 데이터 가져오기 실패 처리
                        Log.e("AuthActivity", "Firestore에서 데이터 가져오기 실패", e)
                        tvID.text = "사용자 데이터를 가져오는 중 오류가 발생했습니다."
                    }
            }
        } else {
            // 사용자가 로그인하지 않은 경우
            button.text = "로그인"
            tvID.text = "로그인되지 않았습니다."
        }

        // UI에 마지막 검색 시간 표시
        displayLastSearchTime()
    }

    private fun saveCurrentTimeToFile() {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val fileName = "search_time.txt"
        val fileContents = "마지막 검색 시간 : $currentTime"

        try {
            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
                Log.d("MainActivity", "Search time saved: $fileContents")
            }
        } catch (e: FileNotFoundException) {
            Log.e("MainActivity", "File not found", e)
        } catch (e: IOException) {
            Log.e("MainActivity", "Error writing to file", e)
        }
    }

    private fun readLastSearchTimeFromFile(): String? {
        val fileName = "search_time.txt"
        var lastSearchTime: String? = null

        try {
            openFileInput(fileName).use {
                lastSearchTime = it.bufferedReader().readLine()
                Log.d("MainActivity", "Last search time read: $lastSearchTime")
            }
        } catch (e: FileNotFoundException) {
            Log.e("MainActivity", "File not found", e)
        } catch (e: IOException) {
            Log.e("MainActivity", "Error reading from file", e)
        }

        return lastSearchTime
    }

    private fun saveCurrentTime() {
        // 현재 시간을 파일에 저장
        saveCurrentTimeToFile()
        Log.d("MainActivity", "Current time saved after successful response")
    }


    private fun displayLastSearchTime() {
        // UI에 마지막 검색 시간 표시
        val lastSearchTime = readLastSearchTimeFromFile()
        binding.lastsaved.text = lastSearchTime ?: "No search made yet."
    }

    private fun applyBackgroundColor() {
        val defaultColor = Color.GREEN // 기본값 설정
        val colorString = sharedPreference.getString("color", "#00ff00") ?: "#00ff00"

        // 색상 문자열이 올바른지 확인하고 파싱
        val color = try {
            Color.parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            Log.e("MainActivity", "Invalid color string: $colorString", e)
            defaultColor // 기본값 반환
        }

        binding.lastsaved.setBackgroundColor(color)
    }

    private fun applyUserPhoto() {
        val userPhotoUrl = sharedPreference.getString("photo", "R.drawable.user") ?: ""
        val resourceId = getResourceIdByName(userPhotoUrl)
        if(resourceId!=0) {
            val headerView = binding.mainDrawerView.getHeaderView(0)
            val userImageView = headerView.findViewById<ImageView>(R.id.profile)
            userImageView.setImageResource(resourceId)
        }
    }

    private fun getResourceIdByName(resourceName: String) : Int{
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }
    private fun applyTextSize() {
        val sizeString = sharedPreference.getString("size", "16.0f") ?: "16.0f"
        val size = sizeString.toFloatOrNull() ?: 16.0f
        binding.lastsaved.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }



    // onResume에서도 설정이 변경될 때마다 반영할 수 있도록 설정
    override fun onResume() {
        super.onResume()
        applyBackgroundColor()
        applyUserSettings()
        applyUserPhoto()
        applyTextSize()
        displayLastSearchTime()
    }

}
