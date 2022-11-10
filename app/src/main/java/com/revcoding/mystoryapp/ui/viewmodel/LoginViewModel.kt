package com.revcoding.mystoryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.revcoding.mystoryapp.api.ApiConfig
import com.revcoding.mystoryapp.data.model.LoginResponse
import com.revcoding.mystoryapp.data.model.LoginResult
import com.revcoding.mystoryapp.data.model.UserModel
import com.revcoding.mystoryapp.data.model.UserPreference
import com.revcoding.mystoryapp.helper.Event
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    private val _isAuthSuccess = MutableLiveData<Event<Boolean>>()
    val isAuthSuccess: LiveData<Event<Boolean>> = _isAuthSuccess

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun login(user: UserModel) {
        viewModelScope.launch {
            pref.login(user)
        }
    }

    fun getUser(email: String, password: String) {
        _isLoading.value = Event(true)
        val client = ApiConfig.getApiService().loginUser(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = Event(false)
                val loginResponse = response.body()
                if (response.isSuccessful) {
                    val userId = loginResponse?.loginResult?.userId
                    val name = loginResponse?.loginResult?.name
                    val token = loginResponse?.loginResult?.token

                    login(UserModel(userId!!, name!!, token!!))

                    _isAuthSuccess.value = Event(true)
                    _snackbarText.value = Event(response.body()?.message.toString())
                } else {
                    _isAuthSuccess.value = Event(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

//    fun getUser(): LiveData<LoginResult> {
//        return pref.getUser().asLiveData()
//    }

    companion object {
        private const val TAG = "LoginViewModel"
    }

}