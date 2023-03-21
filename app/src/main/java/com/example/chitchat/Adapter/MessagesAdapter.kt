package com.example.chitchat.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.ModelClass.Messages
import com.example.chitchat.databinding.ReceiverLayoutItemRvBinding
import com.example.chitchat.databinding.SenderLayoutItemRvBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MessagesAdapter(messageList : ArrayList<Messages>, senderImageUri : String, receiverImageUri : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class senderViewHolder(val binding_sender: SenderLayoutItemRvBinding): RecyclerView.ViewHolder(binding_sender.root){
         fun bind(messages: Messages){
             binding_sender.textMesaages.text = messages.message

             Picasso.with(binding_sender.senderImage.context).load(senderImageUri).into(binding_sender.senderImage)

         }
     }

    inner class receiverViewHolder(val binding_receiver: ReceiverLayoutItemRvBinding): RecyclerView.ViewHolder(binding_receiver.root) {
         fun bind(messages: Messages){
             binding_receiver.textMesaages.text = messages.message

             Picasso.with(binding_receiver.receiverImage.context).load(receiverImageUri).into(binding_receiver.receiverImage)

         }
     }

    var messageList = messageList

    var senderImageUri = senderImageUri
    var receiverImageUri = receiverImageUri

    var auth = Firebase.auth

    var ITEM_SEND = 1
    var ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == ITEM_SEND) {

            return senderViewHolder(
               SenderLayoutItemRvBinding.inflate(
                   LayoutInflater.from(parent.context),parent,false
               )
            )

        } else {

            return receiverViewHolder(
                ReceiverLayoutItemRvBinding.inflate(
                    LayoutInflater.from(parent.context),parent,false
                )
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder.javaClass == senderViewHolder::class.java) {

            var senderHolder = holder as senderViewHolder
            senderHolder.bind(messageList[position])

        } else {

            var receiverHolder = holder as receiverViewHolder
            receiverHolder.bind(messageList[position])

        }



    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        var messages = messageList[position]
        if(auth.currentUser?.uid.equals(messages.senderUid)) {

            return ITEM_SEND

        } else {
            return ITEM_RECEIVE
        }
    }
}