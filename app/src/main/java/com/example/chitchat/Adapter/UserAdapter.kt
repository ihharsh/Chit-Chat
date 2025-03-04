package com.example.chitchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.ModelClass.User
import com.example.chitchat.databinding.ItemUserHomeRvBinding
import com.squareup.picasso.Picasso

class UserAdapter(
    private var userList: MutableList<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemUserHomeRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.userName.text = user.name
            binding.userStatus.text = user.status

            // Load image using Picasso safely
            user.imageUri?.let { imageUrl ->
                if (imageUrl.isNotEmpty()) {
                    Picasso.with(binding.userImage.context).load(imageUrl).into(binding.userImage)
                }
            }

            itemView.setOnClickListener { onUserClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserHomeRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    // Update list dynamically
    fun updateUsers(newUsers: List<User>) {
        userList.clear()
        userList.addAll(newUsers)
        notifyDataSetChanged()
    }
}
