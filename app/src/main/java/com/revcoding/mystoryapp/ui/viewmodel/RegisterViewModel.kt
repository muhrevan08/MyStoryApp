package com.revcoding.mystoryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revcoding.mystoryapp.api.ApiConfig
import com.revcoding.mystoryapp.data.model.RegisterResponse
import com.revcoding.mystoryapp.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {
    private val _isRegisterSuccess = MutableLiveData<Event<Boolean>>()
    val isRegisterSuccess: LiveData<Event<Boolean>> = _isRegisterSuccess

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    private val _snackBarText = MutableLiveData<Event<Boolean>>()
    val snackBarText: LiveData<Event<Boolean>> = _snackBarText

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = Event(true)
        val client = ApiConfig.getApiService().registerUser(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = Event(false)
                if (response.isSuccessful) {
                    _isRegisterSuccess.value = Event(true)
                } else {
                    _isRegisterSuccess.value = Event(false)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }

}