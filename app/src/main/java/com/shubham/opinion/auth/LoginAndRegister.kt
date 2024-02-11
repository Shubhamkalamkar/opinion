package com.shubham.opinion.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shubham.opinion.MainActivity
import com.shubham.opinion.Model.UserData
import com.shubham.opinion.R
import com.shubham.opinion.databinding.ActivityLoginAndRegisterBinding
import com.shubham.opinion.databinding.ActivityWelcomeScreenBinding
import java.io.IOException
import java.util.UUID

class LoginAndRegister : AppCompatActivity() {
    private val binding: ActivityLoginAndRegisterBinding by lazy {
        ActivityLoginAndRegisterBinding.inflate(layoutInflater)
    }

    private val auth = FirebaseAuth.getInstance()

    private val PICK_IMG_REQ=1
    private lateinit var selectedImageUri:Uri
    private lateinit var storageReference: StorageReference
    private lateinit var imageView: ImageView

    private lateinit var userId: String
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        storageReference= FirebaseStorage.getInstance().reference
        imageView = binding.profileImg

        val action = intent.getStringExtra("action")

        if(action!=null){
           when(action) {
               "Login"->{
                   Toast.makeText(this,"login",Toast.LENGTH_SHORT).show()
                   binding.loginEmail.visibility = View.VISIBLE
                   binding.loginPassword.visibility = View.VISIBLE
                   binding.loginBtn.visibility = View.VISIBLE

                   binding.registerName.visibility = View.INVISIBLE
                   binding.registerEmail.visibility = View.INVISIBLE
                   binding.registerPassword.visibility = View.INVISIBLE
                   binding.registerBtn.visibility = View.INVISIBLE
               }
               "Register"->{
                   Toast.makeText(this,"Register",Toast.LENGTH_SHORT).show()
               }
           }
        }

        binding.profileImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type="image/*"
            startActivityForResult(intent,PICK_IMG_REQ)
        }

        binding.loginBtn.setOnClickListener{
            var email = binding.loginEmail.text.toString()
            var password = binding.loginPassword.text.toString()
            if(email.isNullOrBlank()||password.isNullOrBlank()){
//                Toast.makeText(this,"enter all fields",Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.root,"Enter all fields",Snackbar.LENGTH_SHORT).show()
            }
            else{
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){task->
                        if (task.isSuccessful){
                            val user = auth.currentUser
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            // Login failed
                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "Login failed. $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.registerBtn.setOnClickListener{
            var name = binding.registerName.text.toString()
            var email = binding.registerEmail.text.toString()
            var password = binding.registerPassword.text.toString()
            if(name.isNullOrBlank()||email.isNullOrBlank()||password.isNullOrBlank()){
//                Toast.makeText(this,"enter all fields",Toast.LENGTH_SHORT).show()
                Snackbar.make(binding.root,"Enter all fields",Snackbar.LENGTH_SHORT).show()
            } else if (!::selectedImageUri.isInitialized) {
                Snackbar.make(binding.root, "Select an image", Snackbar.LENGTH_SHORT).show()
            }
            else{
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){task->
                        if (task.isSuccessful) {
                            userId = auth.currentUser?.uid?:""
                            uploadImageToFirebaseStorage()
//                            val intent = Intent(this, MainActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                            finish()
                        }

                        else{
                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "Registration failed. $errorMessage", Toast.LENGTH_SHORT).show()

                            // You can also log the error for further investigation
                            Log.e("RegistrationError", "Error: $errorMessage")
//                            Snackbar.make(binding.root,"Registration Failed",Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PICK_IMG_REQ&& resultCode== RESULT_OK&&data!=null&&data.data!=null){
            selectedImageUri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedImageUri)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

   private fun uploadImageToFirebaseStorage(){
//       val imageReference = storageReference.child("ProfileImages/$userId/${UUID.randomUUID()}")
       val imageReference = storageReference.child("ProfileImages/$userId")
       try{
           imageReference.putFile(selectedImageUri)
               .addOnSuccessListener { taskSnapshot->
                   imageReference.downloadUrl.addOnSuccessListener { uri ->
                       imageUrl = uri.toString()
                       Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                       updateUserDataWithImageUrl()
                   }
               }
               .addOnFailureListener { exception ->
                   // Handle failed upload
                   Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
               }
       }
       catch (e: IOException) {
           e.printStackTrace()
       }
    }

    private fun updateUserDataWithImageUrl() {
        // Update the user data in the Firebase Realtime Database
        Log.d("userId",userId)
        Log.d("imgUrl",imageUrl.toString())
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, imageUrl.toString(), Toast.LENGTH_SHORT).show()
        val userData = UserData(binding.registerName.text.toString(), binding.registerEmail.text.toString(), binding.registerPassword.text.toString(), imageUrl.toString())
//        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)
//        userReference.setValue(userData)
        val database = Firebase.database
        val myRef = database.getReference("message")
        myRef.setValue("Hello, World!")
//        val intent = Intent(this, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        finish()
    }
}