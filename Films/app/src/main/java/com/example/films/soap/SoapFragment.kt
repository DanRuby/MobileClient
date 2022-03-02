package com.example.films.soap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.films.R
import com.example.films.databinding.FragmentMainBinding
import com.example.films.data.FilmAdapter
import com.example.films.data.FilmListener
import com.example.films.data.ViewModel
import java.util.*
import kotlin.concurrent.schedule

class SoapFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: ViewModel by activityViewModels()

    private val adapter = FilmAdapter(FilmListener { filmId ->
        viewModel.filmId = filmId
        findNavController().navigate(R.id.action_soapFragment_to_descriptionFragment)
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main, container, false
        )

        viewModel.loadSoapData()
        adapter.data = viewModel.modelMock.value?.films ?: listOf()

        setListeners()
        setObservers()

        binding.fragmentMainRvFilmList.adapter = adapter

        return binding.root
    }

    private fun setListeners() {
        binding.fragmentMainSwipeRefreshLayout.setOnRefreshListener {
            viewModel.loadSoapData()
            binding.fragmentMainSwipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setObservers() {
        viewModel.model.observe(viewLifecycleOwner, {
            if (viewModel.model.value?.films == null) {

                Timer().schedule(5000) {
                    viewModel.loadRestData()
                }
            } else {
                adapter.data = viewModel.model.value?.films ?: listOf()
                adapter.setServerType("SOAP")
            }
        })
    }
}