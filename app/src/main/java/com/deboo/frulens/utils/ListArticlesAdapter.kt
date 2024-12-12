package com.deboo.frulens.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.deboo.frulens.BuildConfig
import com.deboo.frulens.R
import com.deboo.frulens.data.Recipes
import com.deboo.frulens.databinding.ItemArticleBinding

class ListArticlesAdapter(
    private val onItemClicked: (Recipes) -> Unit // Add this parameter to allow item click handling
) : ListAdapter<Recipes, ListArticlesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val up = getItem(position)
        holder.bind(up)
        holder.itemView.setOnClickListener {
            up?.let { recipe -> onItemClicked(recipe) } // Pass the clicked item to the listener
        }
    }

    class MyViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(up: Recipes) {
            // Load the image using Glide
            val imageUrl = BuildConfig.ARTICLES_URL + up.image
            Log.d("ImageUrl", "Loading image from: $imageUrl")  // Log the full URL
            Glide.with(binding.imgItemPhoto.context)
                .load(imageUrl)
                .override(200, 200) // Resize the image to smaller dimensions
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.img_error)
                .into(binding.imgItemPhoto)


            binding.tvItemName.text = up.title
            binding.tvItemDescription.text = up.category.toString()
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Recipes>() {
            override fun areItemsTheSame(oldItem: Recipes, newItem: Recipes): Boolean {
                return oldItem.id == newItem.id // Make sure to compare unique IDs
            }

            override fun areContentsTheSame(oldItem: Recipes, newItem: Recipes): Boolean {
                return oldItem == newItem
            }
        }
    }
}