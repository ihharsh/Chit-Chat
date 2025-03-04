package com.example.chitchat.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitchat.Repository.HomeRepository
import com.example.chitchat.ModelClass.User
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {
    val userList: LiveData<List<User>> = repository.userList

    init {
        fetchUsers()
        updateFcmToken()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            repository.fetchUsers()
        }
    }
    private fun updateFcmToken() {
        repository.updateFcmToken()
    }
}
