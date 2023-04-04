package com.example.chitchat.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.ModelClass.User
import com.example.chitchat.Adapter.UserAdapter
import com.example.chitchat.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    lateinit var binding_home: ActivityHomeBinding
     lateinit var auth: FirebaseAuth
     lateinit var database: FirebaseDatabase
     lateinit var userList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database
        binding_home = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding_home.root)

        userList = ArrayList<User>()


        if(auth.currentUser==null){
            startActivity(Intent(this, ResgistrationActivity::class.java))
        }

        var databaseReference = database.reference.child("user")




        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (snapshot in snapshot.children){

                    val user = snapshot.getValue(User::class.java)
                    if(auth.currentUser?.uid==user?.uid){
                        continue
                    }
                    userList?.add(user!!)

                }

                // Initialize the adapter here
                var adapter = UserAdapter(userList!!)
                binding_home.rvhome.layoutManager = LinearLayoutManager(this@HomeActivity)
                binding_home.rvhome.adapter = adapter


            }

            override fun onCancelled(error: DatabaseError) {

            }

        }


        databaseReference.addValueEventListener(listener)

        binding_home.ivLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding_home.ivSetting.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }













    }
}