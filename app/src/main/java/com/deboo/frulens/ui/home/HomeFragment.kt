package com.deboo.frulens.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deboo.frulens.databinding.FragmentHomeBinding
import com.deboo.frulens.ui.detail.DetailActivity
import com.deboo.frulens.ui.signin.SignInActivity
import com.deboo.frulens.utils.ListArticlesAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListArticlesAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupViewModel()

        return root
    }

    private fun setupRecyclerView() {
        adapter = ListArticlesAdapter { article ->
            // Navigate to DetailActivity with the clicked article
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("recipe", article)  // Pass the Recipe object
            startActivity(intent)
        }
        binding.rvArticle.layoutManager = LinearLayoutManager(context)
        binding.rvArticle.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel.recipes.observe(viewLifecycleOwner) { recipesList ->
            Log.d(TAG, "Recipes: $recipesList")
            adapter.submitList(recipesList)
            binding.articlesection.visibility = View.VISIBLE
            binding.rvArticle.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvArticle.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.fetchRecipes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val TAG = "HomeFragment"
    }
}