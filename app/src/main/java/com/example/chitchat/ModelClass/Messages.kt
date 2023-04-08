package com.example.chitchat.ModelClass

data class Messages(
    var messageID: String = "",
    var message: String ="" ,
    var senderUid: String ="",
    var timestamp: Long = 123456,
    var reaction: Int = -1
)