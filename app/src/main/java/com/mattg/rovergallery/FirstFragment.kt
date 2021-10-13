package com.mattg.rovergallery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.flatMap
import androidx.recyclerview.widget.GridLayoutManager
import com.mattg.rovergallery.databinding.FragmentFirstBinding
import com.mattg.rovergallery.viewModels.PhotosViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var viewModel: PhotosViewModel
    private lateinit var photoAdapter: PhotosAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        CoroutineScope(Dispatchers.IO).launch {
            observeViewModel()
        }

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    /**
     * Observe live data variables
     */
    private suspend fun observeViewModel() {
        viewModel.flow.collectLatest {
            Log.d("DATATEST", it.toString())
            photoAdapter.submitData(it)
        }
    }

    /**
     * Initialize adapter and callback for this view
     */
    private fun initViews() {
        binding.floatingActionButton2.setOnClickListener {
            val roverOptionCallback  = RoverCallback{ rover, position ->
                //TODO
                //viewModel.setRoverSelection(rover)
            }
            val dateCallback = DateCallback {oldDate, newDate ->
                //TODO
                //viewModel.setDateSelection(newDate)
            }
            val doneCallback = CompleteCallback { complete ->
                //TODO
                //viewModel.callCustomSearchApi()
            }
            showBottomSheetSelectionDialog(
                requireContext(),
                roverOptionCallback,
                dateCallback,
                doneCallback
            )
        }
        //handle adapter
        photoAdapter = PhotosAdapter()
        //add callback for adapter results
        val callback = RecyclerCallback { photo, position ->
            //TODO utilize retrieved parameters
        }
        photoAdapter.setCallBack(callback)
        binding.rvImages.apply {
            adapter = photoAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}