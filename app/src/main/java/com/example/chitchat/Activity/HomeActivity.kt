package com.example.chitchat.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.LinearLayoutManager
import coil.compose.rememberAsyncImagePainter
import com.example.chitchat.ModelClass.User
import com.example.chitchat.Adapter.UserAdapter
import com.example.chitchat.databinding.ActivityHomeBinding
import com.example.chitchat.databinding.ActivityHomeComposeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

class HomeActivity : AppCompatActivity() {

     lateinit var binding_home: ActivityHomeBinding
    //lateinit var binding_home_COMPOSE: ActivityHomeComposeBinding

     lateinit var auth: FirebaseAuth
     lateinit var database: FirebaseDatabase
     lateinit var userList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database
       // binding_home_COMPOSE = ActivityHomeComposeBinding.inflate(layoutInflater)
        binding_home = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding_home.root)






        userList = ArrayList<User>()


        if(auth.currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
        }

        var databaseReference = database.reference.child("user")

        Firebase.messaging.token.addOnSuccessListener {token->

            val childUpdateToken = hashMapOf<String,Any>(
                "token" to token
            )


            auth.currentUser?.uid?.let { userId ->
                database.reference
                    .child("user")
                    .child(userId)
                    .updateChildren(childUpdateToken)
            }




        }




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


//                binding_home_COMPOSE.composeLayout.setContent {
//                    UserListComposable(this@HomeActivity).UserListLazyColumn(users = userList)
//
//                }

                // Initialize the adapter here
                var adapter = UserAdapter(userList!!)
                binding_home.rvhome.layoutManager = LinearLayoutManager(this@HomeActivity)
                binding_home.rvhome.adapter = adapter


            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        databaseReference.addValueEventListener(listener)





//        binding_home.ivLogout.setOnClickListener {
//            auth.signOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }

        binding_home.ivSetting.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }



    }





}