package com.example.chitchat.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chitchat.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    lateinit var binding_login: ActivityLoginBinding
    lateinit var auth : FirebaseAuth
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding_login = ActivityLoginBinding.inflate(layoutInflater)
        auth = Firebase.auth

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        setContentView(binding_login.root)


        // if user dont have an account -> register
        binding_login.tvSignUp.setOnClickListener {
            startActivity(Intent(this, ResgistrationActivity::class.java))
        }

        // if user have an account and clicks on signIn after
        // giving credentials
        binding_login.btnSignIn.setOnClickListener {
            progressDialog.show()

            var email = binding_login.etEmail.text.toString()
            var pass = binding_login.etPassword.text.toString()


            // if email or pass is incorrect check the following
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass))
            {
                progressDialog.dismiss()
                Toast.makeText(this,"Enter Valid Data", Toast.LENGTH_SHORT).show()
            }
            else if (!isEmailValid(email))
            {
                progressDialog.dismiss()
                binding_login.etEmail.setError("Invalid Email")
                Toast.makeText(this,"Enter Valid Email", Toast.LENGTH_SHORT).show()
            }
            else if(pass.length<6)
            {
                progressDialog.dismiss()
                binding_login.etPassword.setError("Invalid Password")
                Toast.makeText(this,"Enter Valid Password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                signInUser(email,pass)
            }


        }

    }


    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signInUser(email: String, pass: String){
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                task->

            // if signed in correctly then go to Home
            if (task.isSuccessful){
                progressDialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this,"Error in Login", Toast.LENGTH_SHORT).show()
            }



        }
    }
}