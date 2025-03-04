package com.example.chitchat.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chitchat.ModelClass.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class HomeRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("user")

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> get() = _userList

    fun updateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            val tokenUpdate = mapOf("token" to token)
            auth.currentUser?.uid?.let { userId ->
                database.child(userId).updateChildren(tokenUpdate)
            }
        }
    }

    fun fetchUsers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    if (user != null && user.uid != auth.currentUser?.uid) {
                        users.add(user)
                    }
                }
                _userList.value = users
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
