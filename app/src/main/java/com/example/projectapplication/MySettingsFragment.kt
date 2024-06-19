package com.example.projectapplication

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class MySettingsFragment : PreferenceFragmentCompat() {
    //settingsFragment를 생성하면 res아래에 xml 폴더가 자동 생성
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}