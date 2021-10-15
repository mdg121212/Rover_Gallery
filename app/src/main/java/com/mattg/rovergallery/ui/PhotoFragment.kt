package com.mattg.rovergallery.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mattg.rovergallery.R
import com.mattg.rovergallery.databinding.FragmentSecondBinding
import com.mattg.rovergallery.utils.getProgressDrawable
import com.mattg.rovergallery.utils.loadImage
import com.mattg.rovergallery.viewModels.PhotosViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class PhotoFragment : BaseFragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PhotosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
        (activity as AppCompatActivity).supportActionBar?.title = "Photo"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initViews()
           // findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    fun initViews(){
        binding.fabPhotoDetail.setOnClickListener {
            showBottomSheetDetailDialog(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}