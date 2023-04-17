package com.example.chitchat.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chitchat.Adapter.MessagesAdapter
import com.example.chitchat.ModelClass.Messages
import com.example.chitchat.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext.Response
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.Date

class ChatActivity : AppCompatActivity() {
   // getting these from UserAdapter Intent
    lateinit var receiverUid : String
    lateinit var receiverImageUri : String
    lateinit var receiverName : String
    lateinit var receiverToken: String

    //  binding, auth & database
    lateinit var binding_chat: ActivityChatBinding
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    // senderImageUri == current User
    lateinit var senderImageUri: String

    // for creating two chatrooms for 2 users chatting with each other
    var senderRoom = ""
    var receiverRoom =""

    // arraylist for messages
    var messageList = ArrayList<Messages>()

    var senderName = ""



    lateinit var senderUid : String
    lateinit var adapter: MessagesAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialising them
        binding_chat = ActivityChatBinding.inflate(layoutInflater)
        auth = Firebase.auth
        database = Firebase.database

        setContentView(binding_chat.root)

        getIntentValuesFromHomeActivity()

        // updating chat window user name with recievername
        binding_chat.receiverName.text = receiverName
        // updating chat window image with recieverImage
        var receiverImageView = binding_chat.receiverProfileImage
        Picasso.with(this).load(receiverImageUri).into(receiverImageView)



        // senderUid == current logged in user
         senderUid = auth.uid.toString()

        createChatRoom()

        updateSenderImage()

        UpdateMessagelist()


        binding_chat.ivSendBtn.setOnClickListener {
            var etMessage = binding_chat.etMessage.text.toString()

            if(etMessage.isEmpty()){
                Toast.makeText(this,"Please enter valid message",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding_chat.etMessage.setText("")

            addMessagesToDatabase2(etMessage)



        }

    }

    fun getIntentValuesFromHomeActivity(){
        // getting from intent
        receiverUid = intent.getStringExtra("receiverUid")!!
        receiverName = intent.getStringExtra("receiverName")!!
        receiverImageUri = intent.getStringExtra("receiverImage")!!
        receiverToken = intent.getStringExtra("token")!!

        Toast.makeText(this,receiverToken,Toast.LENGTH_SHORT).show()

    }

    fun createChatRoom() {
        // creating sender and reciever room
        senderRoom = senderUid +  receiverUid
        receiverRoom = receiverUid + senderUid
    }

    fun UpdateMessagelist(){
        // dbref for chats->senderRoom->messages
        var dbReferenceChats = database.reference.child("chats").child(senderRoom).child("messages")
        var listener_senderRoom = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for(snapshot in snapshot.children){
                    val message = snapshot.getValue(Messages::class.java)
                    message?.messageID = snapshot.key.toString()
                    messageList.add(message!!)
                }

                setAdapter()



            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        dbReferenceChats.addValueEventListener(listener_senderRoom)
    }


    fun updateSenderImage(){
        var dbref = database.reference.child("user").child(senderUid)

        var listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                senderImageUri = snapshot.child("imageUri").value.toString()
                senderName = snapshot.child("name").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        dbref.addValueEventListener(listener)
    }

    fun setAdapter(){
        adapter = MessagesAdapter(messageList, senderImageUri, receiverImageUri,receiverRoom,senderRoom)
        var llm = LinearLayoutManager(this@ChatActivity)
        llm.stackFromEnd = true
        binding_chat.rvMessages.layoutManager = llm
        binding_chat.rvMessages.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    fun addMessagesToDatabase2(etMessage: String){
        var date = Date()

        var message = Messages(message = etMessage, senderUid = senderUid, timestamp = date.time)

        val randomkey = database.reference.push().key.toString()

        database.reference.child("chats")
            .child(senderRoom)
            .child("messages")
            .child(randomkey)
            .setValue(message)
            .addOnSuccessListener {


                database.reference.child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(randomkey)
                    .setValue(message).addOnSuccessListener {
                        sendNotification(senderName,message.message)
                    }

            }
    }

    fun sendNotification(name: String, message: String){
        val queue = Volley.newRequestQueue(this)
        val url = "https://fcm.googleapis.com/fcm/send"

        val data = JSONObject()
        data.put("title",name)
        data.put("body",message)

        val notificationData = JSONObject()
        notificationData.put("notification",data)
        notificationData.put("to",receiverToken)

        val request = object : JsonObjectRequest(
            Method.POST, url, notificationData,
            com.android.volley.Response.Listener { response ->
                // Handle the success response

            },
            com.android.volley.Response.ErrorListener { error ->
                // Handle the error response

            }) {
            // add headers here
            override fun getHeaders(): MutableMap<String, String> {
                // Set the request headers
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=AAAArOMRVv0:APA91bFy7IZZTGApgllj5JG38s2nQHq5HrzmJIMT7HiChhubs7eTsF0by5xo25OyrM3FN65OVFLiXBzv_n4x18RspejzFdSp8_3uV_1K31oad6Ab3uxIXH4snqX02zFm8k952m40mRWq"
                headers["Content-Type"] = "application/json"
                return headers
            }

        }

        queue.add(request)




    }




}


