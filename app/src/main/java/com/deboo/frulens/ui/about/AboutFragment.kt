package com.deboo.frulens.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deboo.frulens.R
import com.deboo.frulens.data.team
import com.deboo.frulens.databinding.FragmentAboutBinding
import com.deboo.frulens.utils.ListTeamAdapter

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!! // Safe unwrapping

    private lateinit var rvTeam: RecyclerView
    private val list = ArrayList<team>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val aboutViewModel = ViewModelProvider(this)[AboutViewModel::class.java]

        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvTeam = binding.rvTeam // Access RecyclerView from binding
        rvTeam.setHasFixedSize(true)

        list.addAll(getListHeroes())
        showRecyclerList()

        return root
    }

    private fun getListHeroes(): ArrayList<team> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listmember = ArrayList<team>()
        for (i in dataName.indices) {
            val member = team(dataName[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listmember.add(member)
        }
        return listmember
    }

    private fun showRecyclerList() {
        rvTeam.layoutManager = LinearLayoutManager(requireContext()) // Correct context
        val listHeroAdapter = ListTeamAdapter(list)
        rvTeam.adapter = listHeroAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
