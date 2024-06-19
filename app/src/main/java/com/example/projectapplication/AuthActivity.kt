package com.example.projectapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.projectapplication.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : BaseActivity() {
    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_auth)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeVisibility(intent.getStringExtra("status").toString())
        binding.goSignInBtn.setOnClickListener {    // 회원 가입 Button
            changeVisibility("signin")
        }

        binding.signBtn.setOnClickListener {
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            val name = binding.authNameEditView.text.toString().trim()
            val birthdate = binding.authBirthdateEditView.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    binding.authNameEditView.text.clear()
                    binding.authBirthdateEditView.text.clear()
                    if (task.isSuccessful) {
                        val user = MyApplication.auth.currentUser
                        user?.let {
                            user.sendEmailVerification()
                                .addOnCompleteListener { sendTask ->
                                    if (sendTask.isSuccessful) {
                                        val userInfo = hashMapOf(
                                            "name" to name,
                                            "birthdate" to birthdate,
                                            "emailVerified" to false  // 이메일 인증 여부를 추가
                                        )
                                        MyApplication.db.collection("users").document(user.uid)
                                            .set(userInfo)
                                            .addOnSuccessListener {
                                                Toast.makeText(baseContext, "회원가입 성공! 메일을 확인해주세요", Toast.LENGTH_SHORT).show()
                                                Log.d("mobileapp", "회원가입 성공!")
                                                changeVisibility("logout")
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(baseContext, "추가 정보 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                                Log.d("mobileapp", "추가 정보 저장 실패: ${e.message}")
                                            }
                                    } else {
                                        Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_SHORT).show()
                                        Log.d("mobileapp", "메일 발송 실패")
                                        changeVisibility("logout")
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(baseContext, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "회원가입 실패: ${task.exception?.message}")
                        changeVisibility("logout")
                    }
                }
        }

        binding.loginBtn.setOnClickListener {   // 로그인 Button
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            MyApplication.auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        if(MyApplication.checkAuth()){
                            MyApplication.email = email
                            Log.d("mobileapp", "로그인 성공")
                            finish()
                        }
                        else{
                            Toast.makeText(baseContext,"이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "이메일 인증 안됨")
                        }
                    }
                    else{
                        Toast.makeText(baseContext,"로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("mobileapp", "로그인 실패")
                    }
                }
        }

        binding.logoutBtn.setOnClickListener {  // 로그아웃 Button
            MyApplication.auth.signOut()
            MyApplication.email = null
            Log.d("mobileapp", "로그아웃")
            finish()
        }

        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            Log.d("mobileapp","account1 : ${task.toString()}")
            //Log.d("mobileapp","account2 : ${task.result}")
            try{
                val account = task.getResult(ApiException::class.java)
                val crendential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(crendential)
                    .addOnCompleteListener(this){task ->
                        if(task.isSuccessful){
                            MyApplication.email = account.email
                            Toast.makeText(baseContext,"구글 로그인 성공", Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "구글 로그인 성공")
                            finish()
                        }
                        else{
                            changeVisibility("logout")
                            Toast.makeText(baseContext,"구글 로그인 실패", Toast.LENGTH_SHORT).show()
                            Log.d("mobileapp", "구글 로그인 실패")
                        }
                    }
            }catch (e: ApiException){ // APIException은 이미 지정된 exception말고 custom한 exception을 만들어서 쓰고 싶을때 사용
                changeVisibility("logout")
                Toast.makeText(baseContext,"구글 로그인 Exception : ${e.printStackTrace()},${e.statusCode}",
                    Toast.LENGTH_SHORT).show()
                Log.d("mobileapp", "구글 로그인 Exception : ${e.message}, ${e.statusCode}")
            }
        }

        binding.googleLoginBtn.setOnClickListener { // 구글인증 Button
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // 구글로그인 화면 불러오기 위해 옵션 선택하는 과정
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build()
            val signInIntent = GoogleSignIn.getClient(this,gso).signInIntent
            requestLauncher.launch(signInIntent)
        }
    }

    fun changeVisibility(mode:String){
        if(mode.equals("login")){       // 현재 로그인 상태
            binding.run{
                authMainTextView.text = "정말 로그아웃하시겠습니까?"
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.VISIBLE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.GONE
                authPasswordEditView.visibility = View.GONE
                authNameEditView.visibility = View.GONE
                authBirthdateEditView.visibility = View.GONE
                signBtn.visibility = View.GONE
                loginBtn.visibility= View.GONE
                googleLoginBtn.visibility = View.GONE
            }
        }
        else if(mode.equals("logout")){ // 현재 로그아웃 상태
            binding.run{
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
                authMainTextView.visibility = View.VISIBLE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authNameEditView.visibility = View.GONE
                authBirthdateEditView.visibility = View.GONE
                signBtn.visibility = View.GONE
                loginBtn.visibility= View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
            }
        }
        else if(mode.equals("signin")){    // 회원가입 버튼 클릭 : 회원가입 진행 상태
            binding.run{
                authMainTextView.visibility = View.GONE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authNameEditView.visibility = View.VISIBLE
                authBirthdateEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility= View.GONE
                googleLoginBtn.visibility = View.GONE
            }
        }
    }
    override fun onResume() {
        super.onResume()
        applyUserSettings()
    }
}