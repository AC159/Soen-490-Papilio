package com.soen490chrysalis.papilio.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import kotlinx.coroutines.launch

class UserChatViewModel(private val userRepository : IUserRepository) : ViewModel()
{
    private val logTag = UserChatViewModel::class.java.simpleName
    var userChatToken : MutableLiveData<String> = MutableLiveData<String>()

    fun getCurrentFirebaseUser() : FirebaseUser?
    {
        return userRepository.getUser()
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    fun getNewChatTokenForUser(userFirebaseId : String)
    {
        viewModelScope.launch {
            val chatToken = userRepository.getNewChatTokenForUser(userFirebaseId)
            if (chatToken != null) userChatToken.value = chatToken!!
        }
    }
}