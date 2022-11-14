package com.revcoding.mystoryapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revcoding.mystoryapp.api.ApiConfig
import com.revcoding.mystoryapp.data.model.AddStoryResponse
import com.revcoding.mystoryapp.helper.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel: ViewModel() {
    private val _uploadSuccess = MutableLiveData<Event<Boolean>>()
    val uploadSuccess: LiveData<Event<Boolean>> = _uploadSuccess

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = Event(true)
        val client = ApiConfig.getApiService().addStories("Bearer $token", image, description)
        client.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                _isLoading.value = Event(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _uploadSuccess.value = Event(true)
                    }
                } else {
                    _uploadSuccess.value =  Event(false)
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}