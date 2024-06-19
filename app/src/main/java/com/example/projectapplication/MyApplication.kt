package com.example.projectapplication

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyApplication : MultiDexApplication() {
    companion object {
        lateinit var auth: FirebaseAuth

        var email: String? = null
        var name: String? = null
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage

        fun checkAuth(): Boolean {
            if (!::auth.isInitialized) {
                initializeAuth()
            }
            if (!::db.isInitialized) {
                db = FirebaseFirestore.getInstance()
            }
            val currentUser = auth.currentUser
            return if (currentUser != null) {
                email = currentUser.email
                fetchUserName(currentUser.uid)
                currentUser.isEmailVerified
            } else {
                false
            }
        }

        private fun initializeAuth() {
            auth = Firebase.auth
        }

        private fun fetchUserName(uid: String) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        name = document.getString("name")
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리
                }
        }
    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
        //storage = FirebaseStorage.getInstance()
    }
}