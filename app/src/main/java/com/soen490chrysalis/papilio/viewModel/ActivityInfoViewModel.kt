package com.soen490chrysalis.papilio.viewModel

import androidx.lifecycle.ViewModel
import com.soen490chrysalis.papilio.repository.users.IUserRepository

class ActivityInfoViewModel(private val userRepository : IUserRepository) : ViewModel()
{
    private val logTag = ActivityInfoViewModel::class.java.simpleName
}