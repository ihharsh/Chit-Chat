package com.example.chitchat.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.ModelClass.Messages
import com.example.chitchat.R
import com.example.chitchat.databinding.ReceiverLayoutItemRvBinding
import com.example.chitchat.databinding.SenderLayoutItemRvBinding
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionSelectedListener
import com.github.pgreze.reactions.ReactionsConfigBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MessagesAdapter(
    messageList: ArrayList<Messages>,
    senderImageUri: String,
    receiverImageUri: String,
    receiverRoom: String,
    senderRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    val receiverRoom = receiverRoom
    val senderRoom = senderRoom

    var auth = Firebase.auth
    var database = Firebase.database

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

        var message = messageList[position]


        val reactarray = intArrayOf(
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
        )



        val config = ReactionsConfigBuilder(holder.itemView.context)
            .withReactions(reactarray)
            .build()

        val reactionSelectedListener : ReactionSelectedListener = object : ReactionSelectedListener{
            override fun invoke(pos: Int): Boolean {
                if(holder.javaClass == senderViewHolder::class.java) {
                var senderHolder = holder as senderViewHolder

                    if(pos!=-1){
                        senderHolder.binding_sender.reaction.setImageResource(reactarray[pos])
                        senderHolder.binding_sender.reaction.visibility = View.VISIBLE
                    }


            } else {
                var receiverHolder = holder as receiverViewHolder

                    if(pos!=-1) {
                        receiverHolder.binding_receiver.reaction.setImageResource(reactarray[pos])
                        receiverHolder.binding_receiver.reaction.visibility = View.VISIBLE
                    }


            }

                message.reaction = pos
                database.reference.child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.messageID)
                    .setValue(message)

                database.reference.child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.messageID)
                    .setValue(message)

            return true // true is closing popup, false is requesting a new selection
            }
        }






        val popup = ReactionPopup(
            holder.itemView.context,
            config,
            reactionSelectedListener,
            null // No need for a ReactionPopupStateChangeListener
        )



        if(holder.javaClass == senderViewHolder::class.java)  {

            var senderHolder = holder as senderViewHolder
            senderHolder.bind(messageList[position])

            if(message.reaction>=0){
               // message.reaction = reactarray[message.reaction]
                senderHolder.binding_sender.reaction.setImageResource(reactarray[message.reaction])
                senderHolder.binding_sender.reaction.visibility = View.VISIBLE
            } else {
                senderHolder.binding_sender.reaction.visibility = View.INVISIBLE
            }

            senderHolder.binding_sender.textMesaages.setOnTouchListener(View.OnTouchListener { view, motionEvent ->

                popup.onTouch(view, motionEvent)
                return@OnTouchListener true
            })

        } else {

            var receiverHolder = holder as receiverViewHolder
            receiverHolder.bind(messageList[position])

            if(message.reaction>=0){
               // message.reaction = reactarray[message.reaction]
                receiverHolder.binding_receiver.reaction.setImageResource(reactarray[message.reaction])

                receiverHolder.binding_receiver.reaction.visibility = View.VISIBLE
            } else {
                receiverHolder.binding_receiver.reaction.visibility = View.INVISIBLE
            }


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