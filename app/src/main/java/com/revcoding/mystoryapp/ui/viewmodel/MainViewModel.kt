package com.revcoding.mystoryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revcoding.mystoryapp.api.ApiConfig
import com.revcoding.mystoryapp.data.model.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _userStory = MutableLiveData<StoryResponse>()
    val userStory: LiveData<StoryResponse> = _userStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isDataFound = MutableLiveData<Boolean>()
    val isDataFound: LiveData<Boolean> = _isDataFound

    fun getStory(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllStories("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()?.listStory
                    if (responseBody.isNullOrEmpty()) {
                        _isDataFound.value = false
                    } else {
                        _userStory.value = response.body()
                        _isDataFound.value = true
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }

        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}