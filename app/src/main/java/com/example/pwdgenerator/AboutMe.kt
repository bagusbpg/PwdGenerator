package com.example.pwdgenerator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutMe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_me)
        val actionBar = supportActionBar
        actionBar!!.title = "About"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}