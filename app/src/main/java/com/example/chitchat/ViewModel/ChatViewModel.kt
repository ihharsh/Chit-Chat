package com.example.chitchat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chitchat.Repository.ChatRepository
import com.example.chitchat.ModelClass.Messages


class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    val messages: LiveData<List<Messages>> get() = repository.messages

    private val _receiverDetails = MutableLiveData<Pair<String, String>>()
    val receiverDetails: LiveData<Pair<String, String>> get() = _receiverDetails

    fun fetchMessages(senderRoom: String) {
        repository.getMessages(senderRoom)
    }

    fun sendMessage(senderRoom: String, receiverRoom: String, message: String, senderName: String) {
        repository.sendMessage(senderRoom, receiverRoom, message, senderName)
    }

    fun setReceiverDetails(name: String, imageUri: String) {
        _receiverDetails.value = Pair(name, imageUri)
    }

    fun getSenderUid():String{
        return repository.getsenderUid()
    }


}
