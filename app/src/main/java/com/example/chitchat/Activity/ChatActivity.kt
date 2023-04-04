package com.example.chitchat.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.Adapter.MessagesAdapter
import com.example.chitchat.ModelClass.Messages
import com.example.chitchat.R
import com.example.chitchat.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.Date

class ChatActivity : AppCompatActivity() {
   // getting these from UserAdapter Intent
    lateinit var receiverUid : String
    lateinit var receiverImageUri : String
    lateinit var receiverName : String

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

    val CHANNEL_ID = "GFG"
    val CHANNEL_NAME = "GFG ContentWriting"
    val CHANNEL_DESCRIPTION = "GFG NOTIFICATION"


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
                    messageList.add(message!!)
                }

                setAdapter()



            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        dbReferenceChats.addValueEventListener(listener_senderRoom)
    }

    fun addMessagesToDatabase(etMessage: String){
        var date = Date()

        var message = Messages(message = etMessage, senderUid = senderUid, timestamp = date.time)

        database.reference.child("chats")
            .child(senderRoom)
            .child("messages")
            .push()
            .setValue(message).addOnSuccessListener {
                database.reference.child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .push()
                    .setValue(message)
            }
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
        adapter = MessagesAdapter(messageList, senderImageUri, receiverImageUri)
        var llm = LinearLayoutManager(this@ChatActivity)
        llm.stackFromEnd = true
        binding_chat.rvMessages.layoutManager = llm
        binding_chat.rvMessages.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    fun addMessagesToDatabase2(etMessage: String){
        var date = Date()

        var message = Messages(message = etMessage, senderUid = senderUid, timestamp = date.time)

        database.reference.child("chats")
            .child(senderRoom)
            .child("messages")
            .push()
            .setValue(message)
            .addOnSuccessListener {


                userNotification(etMessage,senderName)


                database.reference.child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .push()
                    .setValue(message)
                Toast.makeText(this@ChatActivity,"test3",Toast.LENGTH_SHORT).show()
            }
    }

    private fun userNotification(etMessage: String, senderName: String) {

       notificationChannel()

        val nBuilder= NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(senderName)
            .setContentText(etMessage)
            .setSmallIcon(R.drawable.profile)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // passing the Bitmap object as an argument
            .build()

        val nManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        nManager.notify(1, nBuilder)



    }

    private fun notificationChannel() {
        // check if the version is equal or greater
        // than android oreo version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // creating notification channel and setting
            // the description of the channel
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = CHANNEL_DESCRIPTION
            }
            // registering the channel to the System
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}


