package com.example.chitchat.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.Activity.ChatActivity
import com.example.chitchat.ModelClass.User
import com.example.chitchat.databinding.ItemUserHomeRvBinding
import com.squareup.picasso.Picasso

class UserAdapter(userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemUserHomeRvBinding): RecyclerView.ViewHolder(binding.root)

    var userList = userList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserHomeRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var name = userList[position].name
        var uid = userList[position].uid
        var image = userList[position].imageUri
        var status = userList[position].status
        var token = userList[position].token

        holder.binding.userName.text = name
        holder.binding.userStatus.text = status

        var userImage = holder.binding.userImage
        var context = userImage.context
        Picasso.with(userImage.context).load(image).into(userImage)

        holder.itemView.setOnClickListener {
            var intent = Intent(context.applicationContext, ChatActivity::class.java)
            intent.putExtra("receiverName",name)
            intent.putExtra("receiverUid",uid)
            intent.putExtra("receiverImage",image)
            intent.putExtra("token",token)

            context.startActivity(intent)


        }

    }
}