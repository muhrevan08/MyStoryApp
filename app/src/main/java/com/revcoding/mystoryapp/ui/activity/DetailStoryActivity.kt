package com.revcoding.mystoryapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.revcoding.mystoryapp.R
import com.revcoding.mystoryapp.data.model.StoryModel
import com.revcoding.mystoryapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.title_story_detail)

        setupDetail()
    }

    private fun setupDetail() {
        val detailStory = intent.getParcelableExtra<StoryModel>(MainActivity.KEY_ID) as StoryModel

        binding.apply {
            Glide.with(applicationContext)
                .load(detailStory.photoUrl)
                .placeholder(R.drawable.no_picture)
                .error(R.drawable.no_picture)
                .into(photoStory)

            tvName.text = detailStory.name
            tvName2.text = detailStory.name
            tvDescription.text = detailStory.description
            tvCreatedAt.text = detailStory.createdAt
            tvStoryID.text = detailStory.id
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}