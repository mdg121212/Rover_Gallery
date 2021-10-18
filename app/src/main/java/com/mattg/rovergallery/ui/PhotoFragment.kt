package com.mattg.rovergallery.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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
        (activity as AppCompatActivity).supportActionBar?.title = "Photo Details"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initViews()
    }

    private fun initViews() {
        binding.fabPhotoDetail.setOnClickListener {
            //get whatever value the manifest event is holding
            viewModel.manifestResponse.value?.peekContent().let { value ->
                    value?.let { showBottomSheetDetailDialog(requireContext(), value) }
                }

        }
        viewModel._selectedPhoto.value.apply {
            binding.ivPhotoLarge.loadImage(this?.img_src, getProgressDrawable(requireContext()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}