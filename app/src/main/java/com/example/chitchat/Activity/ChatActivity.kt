package com.example.chitchat.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chitchat.Adapter.MessagesAdapter
import com.example.chitchat.ViewModel.ChatViewModel
import com.example.chitchat.databinding.ActivityChatBinding
import com.example.chitchat.databinding.ToolbarCustomBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var bindingtoolbar: ToolbarCustomBinding
    private lateinit var receiverUid: String
    private lateinit var senderUid: String
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var receiverToken: String
    private lateinit var  receiverName:String
    private lateinit var receiverImageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        bindingtoolbar = ToolbarCustomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentValuesFromHomeActivity()
        initializeChatRooms()
        setupUI()
        setupObservers()


        binding.ivSendBtn.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                binding.etMessage.setText("")
                chatViewModel.sendMessage(senderRoom, receiverRoom, message, "Sender Name")
                sendNotification("Sender Name", message)
            } else {
                Toast.makeText(this, "Please enter a valid message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIntentValuesFromHomeActivity() {
        receiverUid = intent.getStringExtra("receiverUid")!!
        receiverName = intent.getStringExtra("receiverName")!!
        receiverImageUri = intent.getStringExtra("receiverImage")!!
        receiverToken = intent.getStringExtra("token")!!
        chatViewModel.setReceiverDetails(receiverName, receiverImageUri)
    }

    private fun initializeChatRooms() {
        senderUid = chatViewModel.getSenderUid() // Retrieve from authentication
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        chatViewModel.fetchMessages(senderRoom)
    }

    private fun setupUI() {


        binding.toolbarCustom.toolbarBackButton.setOnClickListener { onBackPressed() }
        binding.toolbarCustom.receiverNameToolbar.text = receiverName
        Picasso.with(this).load(receiverImageUri).into(binding.toolbarCustom.receiverProfileImageToolbar)
    }

    private fun setupObservers() {

        chatViewModel.messages.observe(this, Observer { messages ->
            val adapter = MessagesAdapter(messages, "senderImageUri", receiverImageUri, receiverRoom, senderRoom)
            binding.rvMessages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity).apply { stackFromEnd = true }
                this.adapter = adapter
            }
        })
    }

    private fun sendNotification(name: String, message: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://fcm.googleapis.com/fcm/send"

        val data = JSONObject()
        data.put("title", name)
        data.put("body", message)

        val notificationData = JSONObject()
        notificationData.put("notification", data)
        notificationData.put("to", receiverToken)
        notificationData.put("sound", "default")
        notificationData.put("priority", "high")

        val request = object : JsonObjectRequest(Method.POST, url, notificationData,
            { response ->
                // Handle success response
            },
            { error ->
                // Handle error response
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "key=AAAArOMRVv0:APA91bFy7IZZTGApgllj5JG38s2nQHq5HrzmJIMT7HiChhubs7eTsF0by5xo25OyrM3FN65OVFLiXBzv_n4x18RspejzFdSp8_3uV_1K31oad6Ab3uxIXH4snqX02zFm8k952m40mRWq"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        queue.add(request)
    }
}