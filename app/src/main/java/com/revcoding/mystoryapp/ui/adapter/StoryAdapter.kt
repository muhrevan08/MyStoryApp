package com.revcoding.mystoryapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revcoding.mystoryapp.R
import com.revcoding.mystoryapp.data.model.StoryModel
import com.revcoding.mystoryapp.data.model.StoryResponse
import com.revcoding.mystoryapp.databinding.ListStoryBinding
import com.revcoding.mystoryapp.ui.activity.DetailStoryActivity
import com.revcoding.mystoryapp.ui.activity.MainActivity

class StoryAdapter: RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    private val listStory = ArrayList<StoryResponse.Story>()

    fun setDataStoryList(listStoryUser: List<StoryResponse.Story>) {
        this.listStory.clear()
        this.listStory.addAll(listStoryUser)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    class ListViewHolder (
        private val binding: ListStoryBinding
        ): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryResponse.Story) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.no_picture)
                    .error(R.drawable.no_picture)
                    .into(photoStory)

                name.text = story.name
                createdAt.text = story.createdAt
                storyID.text = story.id
            }
            itemView.setOnClickListener {
                val storyModel = StoryModel()
                storyModel.photoUrl = story.photoUrl
                storyModel.name = story.name
                storyModel.description = story.description
                storyModel.createdAt = story.createdAt
                storyModel.id = story.id

                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(MainActivity.KEY_ID, storyModel)
                itemView.context.startActivity(intent)
            }
        }
    }
}