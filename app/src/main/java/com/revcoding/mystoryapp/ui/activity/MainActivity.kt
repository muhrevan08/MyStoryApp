package com.revcoding.mystoryapp.ui.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.revcoding.mystoryapp.data.model.StoryResponse
import com.revcoding.mystoryapp.databinding.ActivityMainBinding
import com.revcoding.mystoryapp.ui.adapter.StoryAdapter
import com.revcoding.mystoryapp.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private val storyViewModel: MainViewModel by viewModels()
    private var doubleClickBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story"

        binding.rvStory.setHasFixedSize(true)

        storyViewModel.userStory.observe(this) { story ->
            setListStory(story)
        }

        storyViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        storyViewModel.isDataFound.observe(this) {
            onDataFound(it)
        }


        storyAdapter = StoryAdapter()
        binding.rvStory.adapter = storyAdapter

        showRecyclerView()
    }

    private fun setListStory(storyList: StoryResponse) {
        storyAdapter.setDataStoryList(storyList.listStory)
    }

    private fun showRecyclerView() {
        showLoading(false)
        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                rvStory.layoutManager = GridLayoutManager(applicationContext, 2)
            } else {
                rvStory.layoutManager = layoutManager
            }
            val itemDecoration = DividerItemDecoration(applicationContext, layoutManager.orientation)
            rvStory.addItemDecoration(itemDecoration)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                rvStory.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.INVISIBLE
                fabAddStory.visibility = View.VISIBLE
            }
        }
    }

    private fun onDataFound(found: Boolean) {
        binding.apply {
            if (found) {
                rvStory.visibility = View.VISIBLE
            } else {
                rvStory.visibility = View.INVISIBLE

                Toast.makeText(
                    this@MainActivity,
                    "Story Not Found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        if (doubleClickBack) {
            super.onBackPressed()
            finish()
        } else {
            doubleClickBack = true
            Toast.makeText(
                this,
                "Press One More Time To Out",
                Toast.LENGTH_SHORT
            ).show()
        }
        Handler(mainLooper).postDelayed( { doubleClickBack = false }, DURATION_CLICK_BACK)
    }

//    private fun setListStory(items: List<Story>) {
//        val listStory = ArrayList<Story>()
//        for (data in items) {
//            val story = Story(data.photoUrl, data.name, data.createdAt, data.id)
//            listStory.addAll(listOf(story))
//        }
//        val storyAdapter = StoryAdapter(listStory)
//        binding.rvStory.adapter = storyAdapter
//
//        storyAdapter.
//    }

    companion object {

        private const val TAG = "MainActivity"
        const val KEY_ID = "id"
        const val DURATION_CLICK_BACK = 2000L
    }
}