package com.example.films.description

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.films.R
import com.example.films.databinding.FragmentDescriptionBinding
import com.example.films.data.ViewModel

class DescriptionFragment : Fragment() {

    private lateinit var binding: FragmentDescriptionBinding
    private val viewModel: ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_description, container, false
        )

        if (viewModel.model.value?.films != null)
            for (film in viewModel.model.value?.films!!) {
                if (viewModel.filmId == film.id) {
                    Glide.with(requireContext())

                        .load(film.image)
                        .placeholder(R.color.white_grey)
                        .into(binding.fragmentDescriptionImgImage)

                    binding.fragmentDescriptionTvName.text = film.name
                    binding.fragmentDescriptionTvYear.text = film.year.toString()
                    binding.fragmentDescriptionTvProducer.text = film.producer
                    binding.fragmentDescriptionTvDescription.text = film.description
                }
            }

        setListeners()
        setObservers()
        
        return binding.root
    }

    private fun setListeners() {
    }

    private fun setObservers() {
    }
}