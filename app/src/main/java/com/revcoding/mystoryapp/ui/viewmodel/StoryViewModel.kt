package com.revcoding.mystoryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revcoding.mystoryapp.api.ApiConfig
import com.revcoding.mystoryapp.data.model.StoryResponse
import com.revcoding.mystoryapp.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel: ViewModel() {

    private val _userListStory = MutableLiveData<StoryResponse>()
    val userListStory: LiveData<StoryResponse> = _userListStory

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    fun getStory(token: String) {
        _isLoading.value = Event(true)
        val client = ApiConfig.getApiService().getAllStories("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = Event(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()?.listStory
                    if (!responseBody.isNullOrEmpty()) {
                        _userListStory.value = response.body()
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "StoryViewModel"
    }
}