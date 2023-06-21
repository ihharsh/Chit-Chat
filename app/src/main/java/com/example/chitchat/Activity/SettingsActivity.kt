package com.example.chitchat.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chitchat.ModelClass.User
import com.example.chitchat.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class SettingsActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var storage : FirebaseStorage

    val IMAGE_REQUEST_CODE = 1

    var selectedImageUri : Uri? = null
    var email = ""

    lateinit var progressDialog: ProgressDialog

    lateinit var binding_setting: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_setting = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding_setting.root)

        auth = Firebase.auth
        database = Firebase.database
        storage = Firebase.storage

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        val dbReference = database.reference.child("user").child(auth.uid!!)
        val storageReference = storage.reference.child("upload").child(auth.uid!!)

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var name = snapshot.child("name").value.toString()
                var status = snapshot.child("status").value.toString()
                var imageUri = snapshot.child("imageUri").value.toString()
                email = snapshot.child("email").value.toString()


                binding_setting.settingName.setText(name)
                binding_setting.settingStatus.setText(status)
                Picasso.with(this@SettingsActivity).load(imageUri).into(binding_setting.profileImage)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        dbReference.addValueEventListener(eventListener)

        binding_setting.profileImage.setOnClickListener {
            pickImage()
        }

        binding_setting.save.setOnClickListener {

            progressDialog.show()
            var name = binding_setting.settingName.text.toString()
            var status = binding_setting.settingStatus.text.toString()

            updateValueInFireBase(name,status,storageReference,dbReference)
        }

        binding_setting.ivLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


    }

    private fun updateValueInFireBase(
        name: String,
        status: String,
        storageReference: StorageReference,
        dbReference: DatabaseReference
    ) {

        if(selectedImageUri!=null) {

            storageReference.putFile(selectedImageUri!!).addOnCompleteListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    var updatedImageUri = it.toString()
                    var user = User(auth.uid!!, name,email,updatedImageUri,status)

                    dbReference.setValue(user).addOnCompleteListener {task->
                        if(task.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(this,"Data Updated",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this,HomeActivity::class.java))
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

        } else {

            storageReference.downloadUrl.addOnSuccessListener {
                var updatedImageUri = it.toString()
                var user = User(auth.uid!!, name,email,updatedImageUri,status)

                dbReference.setValue(user).addOnCompleteListener {task->
                    if(task.isSuccessful) {
                        progressDialog.dismiss()
                        Toast.makeText(this,"Data Updated",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,HomeActivity::class.java))
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show()
                    }

                }
            }



        }







    }


    private fun pickImage() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data==null) return

        if(requestCode == IMAGE_REQUEST_CODE){

            selectedImageUri = data?.data!!
            binding_setting.profileImage.setImageURI(selectedImageUri)


        }
    }
}