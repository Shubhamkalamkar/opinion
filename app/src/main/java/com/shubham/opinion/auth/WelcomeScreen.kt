package com.shubham.opinion.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shubham.opinion.R
import com.shubham.opinion.databinding.ActivityWelcomeScreenBinding

class WelcomeScreen : AppCompatActivity() {
    private val binding: ActivityWelcomeScreenBinding by lazy {
        ActivityWelcomeScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.welcomeLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginAndRegister::class.java)
            intent.putExtra("action", "Login")
            startActivity(intent)
        }

        binding.welcomeRegisterBtn.setOnClickListener {
            val intent = Intent(this, LoginAndRegister::class.java)
            intent.putExtra("action", "Register")
            startActivity(intent)
        }
    }
}
