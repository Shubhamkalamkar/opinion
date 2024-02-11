package com.shubham.opinion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.shubham.opinion.auth.WelcomeScreen

class SplashScreen : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
//            checkAuthState()
            startActivity(Intent(this, WelcomeScreen::class.java))

        },3000)
    }
    fun checkAuthState(){
        val currentUser = auth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
        else{
            startActivity(Intent(this, WelcomeScreen::class.java))
        }
        finish()
    }
}