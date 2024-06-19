package com.example.projectapplication

import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager

open class BaseActivity : AppCompatActivity() {
    protected lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        // 베이스 액티비티의 공통 초기화 로직
        applyUserSettings()
        // 기타 공통 설정 초기화 로직 추가
    }

    protected open fun applyUserSettings() {
        // 폰트 설정 가져오기
        val fontKey = sharedPreference.getString("font", "default")

        val fontResourceId = getFontResourceId(fontKey)
        Log.d("BaseActivity", "Selected font resource ID: $fontResourceId")

        // 폰트 로드
        val typeface = ResourcesCompat.getFont(this, fontResourceId)
        if (typeface == null) {
            Log.e("BaseActivity", "Failed to load typeface for font resource ID: $fontResourceId")
        }

        // 모든 텍스트뷰에 폰트 적용
        applyFontRecursively(window.decorView.rootView, typeface)
    }

    private fun getFontResourceId(fontKey: String?): Int {
        return when (fontKey) {
            "mandoddobak" -> R.font.mandoddobak
            "gmarket" -> R.font.gmarket
            "chosun" -> R.font.chosun
            else -> R.font.default_font // 기본 폰트
        }
    }

    private fun applyFontRecursively(view: View, typeface: Typeface?) {
        if (view is TextView) {
            view.typeface = typeface
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                applyFontRecursively(view.getChildAt(i), typeface)
            }
        }
    }

}