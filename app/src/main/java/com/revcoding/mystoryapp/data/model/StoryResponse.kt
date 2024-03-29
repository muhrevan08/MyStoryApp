package com.revcoding.mystoryapp.data.model

import com.google.gson.annotations.SerializedName

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

data class AddStoryResponse(
	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)



