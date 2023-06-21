package com.example.chitchat.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.chitchat.ModelClass.User
import com.example.chitchat.R
import com.example.chitchat.databinding.ActivityResgistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ResgistrationActivity : AppCompatActivity() {

    lateinit var binding_registration: ActivityResgistrationBinding
    lateinit var auth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var storage : FirebaseStorage

    var imageUri: Uri? = null

    lateinit var imageUriString: String
    lateinit var progressDialog: ProgressDialog

    val IMAGE_REQUEST_CODE = 11
    val defaultStatus: String = "Hey there, I'm using Chit-Chat"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_registration = ActivityResgistrationBinding.inflate(layoutInflater)

        auth = Firebase.auth
        database = Firebase.database
        storage = Firebase.storage

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)


        setContentView(binding_registration.root)

        initializeAnimation()



        binding_registration.btnSignUp.setOnClickListener {

            progressDialog.show()
            var name = binding_registration.etName.text.toString()
            var email = binding_registration.etEmail.text.toString()
            var pass = binding_registration.etPassword.text.toString()
            var confPass = binding_registration.etConfirmPassword.text.toString()


            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                progressDialog.dismiss()
                Toast.makeText(this, "Enter Valid Data", Toast.LENGTH_SHORT).show()
            } else if (!isEmailValid(email)) {
                progressDialog.dismiss()
                binding_registration.etEmail.setError("Invalid Email")
                Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show()
            } else if (pass.length < 6 || !pass.equals(confPass)) {
                progressDialog.dismiss()
                binding_registration.etPassword.setError("Invalid Password")
                Toast.makeText(this, "Enter Valid Password", Toast.LENGTH_SHORT).show()
            } else {
                createUser(email, pass, name)
            }

        }

        binding_registration.tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding_registration.profileImage.setOnClickListener {
            pickImage()
        }


    }

    private fun initializeAnimation() {
        var fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        var bottom_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        binding_registration.toplinearLayout.animation = bottom_down

        var handler = Handler()
        var runnable = Runnable {binding_registration.btnSignUp.animation = fade_in
            binding_registration.profileImage.animation = fade_in
            binding_registration.llSignin.animation = fade_in

            binding_registration.tvChitchat.animation = fade_in
            binding_registration.cardView.animation = fade_in
            binding_registration.cvLogo.animation = fade_in  }

        handler.postDelayed(runnable,1000)
    }


    private fun createUser(email: String, pass: String, name: String) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->

            // if user created succesfully then go to Home
            if (task.isSuccessful) {

                var databaseReference = database.reference.child("user").child(auth.uid!!)
                var storageReference = storage.reference.child("upload").child(auth.uid!!)

                if(imageUri!=null){
                    storageReference.putFile(imageUri!!).addOnCompleteListener {
                        task->

                       if(task.isSuccessful) {

                           storageReference.downloadUrl.addOnSuccessListener {
                               Uri->
                               imageUriString =  Uri.toString()
                               var user = User(auth.uid!!, name,email,imageUriString,defaultStatus)

                               databaseReference.setValue(user).addOnCompleteListener {
                                   task->
                                   if(task.isSuccessful){
                                       progressDialog.dismiss()
                                       startActivity(Intent(this, HomeActivity::class.java))
                                       finish()
                                   } else {
                                       Toast.makeText(this, "Enter in creating a new User", Toast.LENGTH_SHORT).show()
                                   }
                               }


                           }
                       }
                    }
                }

                else{
                    imageUriString =  "https://firebasestorage.googleapis.com/v0/b/chit-chat-b3a27.appspot.com/o/profile.jpeg?alt=media&token=2b155264-ea00-44c6-8db5-51c830b11e65"
                    var user = User(auth.uid!!, name,email,imageUriString,defaultStatus)

                    databaseReference.setValue(user).addOnCompleteListener {
                            task->
                        if(task.isSuccessful){
                            progressDialog.dismiss()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Enter in creating a new User", Toast.LENGTH_SHORT).show()
                        }
                    }

                }




            }
        }


    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun pickImage() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST_CODE){

                imageUri = data?.data!!
               binding_registration.profileImage.setImageURI(imageUri)


        }
    }

}