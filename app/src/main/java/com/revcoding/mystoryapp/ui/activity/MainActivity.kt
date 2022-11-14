package com.revcoding.mystoryapp.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.revcoding.mystoryapp.R
import com.revcoding.mystoryapp.ViewModelFactory
import com.revcoding.mystoryapp.data.model.StoryResponse
import com.revcoding.mystoryapp.data.model.UserPreference
import com.revcoding.mystoryapp.databinding.ActivityMainBinding
import com.revcoding.mystoryapp.ui.adapter.StoryAdapter
import com.revcoding.mystoryapp.ui.viewmodel.MainViewModel
import com.revcoding.mystoryapp.ui.viewmodel.StoryViewModel

class MainActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyAdapter = StoryAdapter()
        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.adapter = storyAdapter

        setupViewModel()
        showRecyclerView()

        binding.fabAddStory.setOnClickListener {
            val moveToAddStory = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(moveToAddStory, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.reload -> {
                reloadListStory()
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                logoutUser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewModel() {
        // MainViewModel
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.token.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                storyViewModel.getStory(user.token)
            }
        }

        // StoryViewModel
        storyViewModel = ViewModelProvider(this)[StoryViewModel::class.java]

        storyViewModel.userListStory.observe(this) { story ->
            setListStory(story)
        }

        storyViewModel.isLoading.observe(this) {
            it.getContentIfNotHandled()?.let { state ->
                showLoading(state)
            }
        }

        storyViewModel.isFailed.observe(this) {
            it.getContentIfNotHandled()?.let { failed ->
                isFailed(failed)
            }
        }
    }

    private fun setListStory(storyList: StoryResponse) {
        storyAdapter.setDataStoryList(storyList.listStory)
    }

    private fun reloadListStory() {
        mainViewModel.getUser().observe(this) { user ->
            storyViewModel.getStory(user.token)
        }

        storyViewModel.userListStory.observe(this) { story ->
            setListStory(story)
        }

        storyViewModel.isLoading.observe(this) {
            it.getContentIfNotHandled()?.let { state ->
                showLoading(state)
            }
        }

        storyViewModel.isFailed.observe(this) {
            it.getContentIfNotHandled()?.let { failed ->
                isFailed(failed)
            }
        }

    }

    private fun logoutUser() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_confirmation))
            setMessage(getString(R.string.message_are_you_sure_want_to_exit))
            setPositiveButton(getString(R.string.logout)) { _, _ ->
                mainViewModel.logout()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel()}
            create()
            show()
        }
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

                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                progressBar.visibility = View.INVISIBLE
                rvStory.visibility = View.VISIBLE

                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    private fun isFailed(failed: Boolean) {
        if (failed) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_failure))
                setMessage(getString(R.string.message_there_is_problem))
                setPositiveButton(getString(R.string.reload)) { _, _ -> reloadListStory() }
                create()
                show()
            }
        }
    }

    companion object {
        const val KEY_ID = "id"
    }
}