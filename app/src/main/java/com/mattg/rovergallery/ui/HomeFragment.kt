package com.mattg.rovergallery.ui

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mattg.rovergallery.*
import com.mattg.rovergallery.databinding.FragmentFirstBinding
import com.mattg.rovergallery.utils.EventObserver
import com.mattg.rovergallery.viewModels.PhotosViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : BaseFragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var viewModel: PhotosViewModel
    private lateinit var photoAdapter: PhotosAdapter
    private val binding get() = _binding!!
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        CoroutineScope(Dispatchers.IO).launch {
            observeSearch()

        }
        observeViewModel()
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    /**
     * Observe live data variables
     */
    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.apply {
                Log.d("EVENTCHECK", "toast event received")
                Toast.makeText(requireContext(), this, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.dialogEvent.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.apply {
                Log.d("EVENTCHECK", "dialog event received")
                 closeBottomSheet()
            }
        }
        viewModel.spinnerEvent.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.apply {
                Log.d("EVENTCHECK", "spinner event received")
                when(this){
                    0 ->
                    {
                        Log.d("EVENTCHECK", "spinner event received setting gone")
                        binding.progressMain.visibility = View.GONE
                    }
                    1 ->
                    {
                        Log.d("EVENTCHECK", "spinner event received setting visible")
                        binding.progressMain.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private suspend fun observeSearch() {
        viewModel.getFlow()?.collectLatest {
            photoAdapter.submitData(it)
        }
    }

    /**
     * Initialize adapter and callback for this view
     */
    private fun initViews() {
        binding.floatingActionButton2.setOnClickListener {
            val roverOptionCallback  = RoverCallback{ rover, position ->
                Log.d("EVENTCHECK", "rover callback received")
                viewModel.setRoverSelection(rover)
            }
            val dateCallback = DateCallback {oldDate, newDate ->
                Log.d("EVENTCHECK", "date callback received")
                viewModel.setDateSelection(newDate)
                binding.tvMainDate.setText(newDate.toString())
            }
            val doneCallback = CompleteCallback { complete ->
                viewModel.closeDialog()
                viewModel.toggleSpinner()
                //closeBottomSheet()
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("EVENTCHECK", "done callback received")
                    observeSearch()
                }
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
            viewModel.setPhoto(photo)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}