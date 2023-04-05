package com.example.chitchat.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.ModelClass.Messages
import com.example.chitchat.R
import com.example.chitchat.databinding.ReceiverLayoutItemRvBinding
import com.example.chitchat.databinding.SenderLayoutItemRvBinding
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions
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


        val config = reactionConfig(holder.itemView.context) {
            reactions {
                resId    { R.drawable.ic_fb_like }
                resId    { R.drawable.ic_fb_love }
                resId    { R.drawable.ic_fb_laugh }
                reaction { R.drawable.ic_fb_wow scale ImageView.ScaleType.FIT_XY }
                reaction { R.drawable.ic_fb_sad scale ImageView.ScaleType.FIT_XY }
                reaction { R.drawable.ic_fb_angry scale ImageView.ScaleType.FIT_XY }
            }
        }

        val popup = ReactionPopup(holder.itemView.context, config) { pos -> true.also {
            // position = -1 if no selection
        } }


        if(holder.javaClass == senderViewHolder::class.java) {

            var senderHolder = holder as senderViewHolder
            senderHolder.bind(messageList[position])

            senderHolder.binding_sender.textMesaages.setOnTouchListener(View.OnTouchListener { view, motionEvent ->

                popup.onTouch(view, motionEvent)
                return@OnTouchListener true
            })

        } else {

            var receiverHolder = holder as receiverViewHolder
            receiverHolder.bind(messageList[position])

            receiverHolder.binding_receiver.textMesaages.setOnTouchListener(View.OnTouchListener { view, motionEvent ->

                popup.onTouch(view, motionEvent)
                return@OnTouchListener true
            })


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