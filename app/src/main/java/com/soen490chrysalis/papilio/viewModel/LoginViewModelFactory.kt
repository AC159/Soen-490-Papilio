package com.soen490chrysalis.papilio.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.soen490chrysalis.papilio.repository.users.IUserRepository
import com.soen490chrysalis.papilio.repository.users.UserRepository

class LoginViewModelFactory : ViewModelProvider.NewInstanceFactory()
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) : T
    {
        // todo: Here we are creating a new UserRepository instance
        val userRepository : IUserRepository = UserRepository()
        return LoginViewModel(userRepository) as T
    }
}