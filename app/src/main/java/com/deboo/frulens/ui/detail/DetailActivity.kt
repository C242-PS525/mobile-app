package com.deboo.frulens.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.deboo.frulens.R
import com.deboo.frulens.data.Recipes
import com.bumptech.glide.Glide
import com.deboo.frulens.BuildConfig

class DetailActivity : AppCompatActivity() {

    private lateinit var ivRecipeImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvPrepTimeAndServing: TextView
    private lateinit var tvIngredients: TextView
    private lateinit var tvSteps: TextView
    private lateinit var tvCategory: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize views
        ivRecipeImage = findViewById(R.id.ivRecipeImage)
        tvTitle = findViewById(R.id.tvTitle)
        tvPrepTimeAndServing = findViewById(R.id.tvPrepTimeAndServing)
        tvIngredients = findViewById(R.id.tvIngredients)
        tvSteps = findViewById(R.id.tvSteps)
        tvCategory = findViewById(R.id.tvCategory)

        // Get the Recipe object passed through Intent
        val recipe = intent.getParcelableExtra<Recipes>("recipe")

        if (recipe != null) {
            // Populate the UI with recipe data
            tvTitle.text = recipe.title
            tvPrepTimeAndServing.text = "Preparation Time: ${recipe.prep_time} | Serving: ${recipe.serving}"
            tvIngredients.text = recipe.ingredients.joinToString("\n") { "• $it" }
            tvSteps.text = recipe.steps.joinToString("\n") { "- $it" }
            tvCategory.text = recipe.category.joinToString(", ")

            val imageUrl = BuildConfig.ARTICLES_URL + recipe.image
            Log.d("ImageUrl", "Loading image from: $imageUrl")  // Log the full URL
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.img_error)
                .into(ivRecipeImage)


        }
    }
}
