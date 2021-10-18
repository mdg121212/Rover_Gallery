package com.mattg.rovergallery.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.mattg.rovergallery.CompleteCallback
import com.mattg.rovergallery.PhotosAdapter
import com.mattg.rovergallery.RecyclerCallback
import com.mattg.rovergallery.databinding.FragmentFirstBinding
import com.mattg.rovergallery.viewModels.PhotosViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class HomeFragment : BaseFragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var viewModel: PhotosViewModel
    private lateinit var photoAdapter: PhotosAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clError.visibility = View.GONE
        initViews()
        CoroutineScope(Dispatchers.IO).launch {
            initViewModel()
            observeSearch()
        }
        observeViewModel()

    }

    //get a default manifest for rover curiosity
    private fun initViewModel() {
        viewModel.getManifest("Curiosity")
    }

    /**
     * Observe live data variables
     */
    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.apply {
                Toast.makeText(requireContext(), this, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.dialogEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.apply {
                closeBottomSheet()
            }
        }
        viewModel.spinnerEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.apply {
                when (this) {
                    0 -> {
                        binding.progressMain.visibility = View.GONE
                    }
                    1 -> {
                        binding.progressMain.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private suspend fun observeSearch() {
        viewModel.getFlow().collectLatest {
            photoAdapter.submitData(it)
            photoAdapter.apply {
                addLoadStateListener { loadState ->
                    val isEmptyList =
                        loadState.refresh is LoadState.NotLoading && this.itemCount == 0
                    handleNoData(isEmptyList)
                }
            }
        }
    }

    /**
     * Initialize adapter and callback for this view
     */
    private fun initViews() {
        binding.btnDateSearch.setOnClickListener {
            val doneCallback = CompleteCallback { complete ->
                viewModel.closeDialog()
                viewModel.toggleSpinner()
                CoroutineScope(Dispatchers.IO).launch {
                    observeSearch()
                }
            }
            showDateSelectDialog(requireContext(), doneCallback)
        }
        binding.floatingActionButton2.setOnClickListener {
            val doneCallback = CompleteCallback { complete ->
                viewModel.closeDialog()
                viewModel.toggleSpinner()
                CoroutineScope(Dispatchers.IO).launch {
                    observeSearch()
                }
            }
            showRoverSelectDialog(requireContext(), doneCallback)
        }
        //handle adapter
        photoAdapter = PhotosAdapter()
        //add callback for adapter results
        val callback = RecyclerCallback { photo, position ->
            viewModel.setPhoto(photo)
            findNavController().navigate(com.mattg.rovergallery.R.id.action_FirstFragment_to_SecondFragment)
        }
        photoAdapter.apply {
            setCallBack(callback)
            setContext(requireContext())
        }

        binding.rvImages.apply {
            adapter = photoAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun handleNoData(emptyList: Boolean) {
        when (emptyList) {
            true -> {
                binding.rvImages.visibility = View.INVISIBLE
                binding.clError.visibility = View.VISIBLE
            }

            false -> {
                binding.rvImages.visibility = View.VISIBLE
                binding.clError.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}