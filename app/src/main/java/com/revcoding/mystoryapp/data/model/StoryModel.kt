package com.revcoding.mystoryapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel (
    var name: String? = null,
    var description: String? = null,
    var photoUrl: String? = null,
    var createdAt: String? = null,
    var id: String? = null
): Parcelable