package com.example.chitchat.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chitchat.ModelClass.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class ChatRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val senderUid = auth.uid.toString()

    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages

    fun getsenderUid() : String{
        return senderUid
    }

    fun getMessages(senderRoom: String) {
        val messagesRef = database.reference.child("chats").child(senderRoom).child("messages")
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<Messages>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Messages::class.java)
                    message?.let {
                        it.messageID = messageSnapshot.key.toString()
                        messageList.add(it)
                    }
                }
                _messages.value = messageList
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendMessage(senderRoom: String, receiverRoom: String, message: String, senderName: String) {
        val messageObj = Messages(message = message, senderUid = senderUid, timestamp = Date().time)
        val randomKey = database.reference.push().key.toString()

        val chatRef = database.reference.child("chats")
        chatRef.child(senderRoom).child("messages").child(randomKey).setValue(messageObj)
            .addOnSuccessListener {
                chatRef.child(receiverRoom).child("messages").child(randomKey).setValue(messageObj)
            }
    }
}
