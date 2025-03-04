package com.example.chitchat.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chitchat.Repository.HomeRepository
import com.example.chitchat.ViewModel.HomeViewModel
import com.example.chitchat.ViewModelFactory.HomeViewModelFactory
import com.example.chitchat.adapter.UserAdapter
import com.example.chitchat.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userAdapter: UserAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(HomeRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView
        userAdapter = UserAdapter(mutableListOf()) { user ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("receiverName", user.name)
                putExtra("receiverUid", user.uid)
                putExtra("receiverImage", user.imageUri)
                putExtra("token", user.token)
            }
            startActivity(intent)
        }
        binding.rvhome.layoutManager = LinearLayoutManager(this)
        binding.rvhome.adapter = userAdapter

        // Observe LiveData
        homeViewModel.userList.observe(this) { users ->
            userAdapter.updateUsers(users)
        }

        binding.ivSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        checkUserAuthentication()
    }

    private fun checkUserAuthentication() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
