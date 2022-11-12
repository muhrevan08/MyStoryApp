package com.revcoding.mystoryapp.ui.viewmodel

import androidx.lifecycle.*
import com.revcoding.mystoryapp.data.model.UserModel
import com.revcoding.mystoryapp.data.model.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference): ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}