package com.revcoding.mystoryapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<Story>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) {
	data class Story(

		@field:SerializedName("photoUrl")
		val photoUrl: String? = null,

		@field:SerializedName("createdAt")
		val createdAt: String? = null,

		@field:SerializedName("name")
		val name: String? = null,

		@field:SerializedName("description")
		val description: String? = null,

		@field:SerializedName("id")
		val id: String? = null
	)
}



