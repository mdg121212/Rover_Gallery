package com.mattg.rovergallery.ui

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mattg.rovergallery.*
import com.mattg.rovergallery.databinding.FragmentFirstBinding
import com.mattg.rovergallery.utils.EventObserver
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
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            observeViewModel()
        }


    }

    /**
     * Observe live data variables
     */
    private suspend fun observeViewModel() {
            viewModel.eventsFlow.collectLatest {
                when(it) {
                    PhotosViewModel.Event.ToggleProgress -> {
                        Log.d("FLOWEVENT", "progress")
                    }
                    is PhotosViewModel.Event.ShowToast -> {
                        Log.d("FLOWEVENT", "show toast")

                    }
                    PhotosViewModel.Event.CloseDialog -> {
                        Log.d("FLOWEVENT", "close dialog")
                    }
                }
            }
//        viewModel.toastEvent.observe(viewLifecycleOwner) {
//            it.getContentIfNotHandled()?.apply {
//                Toast.makeText(requireContext(), it.getContentIfNotHandled().toString(), Toast.LENGTH_SHORT).show()
//            }
//        }
//        viewModel.closeDialogEvent.observe(viewLifecycleOwner){
//            it.getContentIfNotHandled()?.apply {
//                if(it.getContentIfNotHandled() == true) closeBottomSheet()
//            }
//        }
//        viewModel.mainSpinnerVisible.observe(viewLifecycleOwner){
//            it.getContentIfNotHandled()?.apply {
//               if (it.getContentIfNotHandled() == true)
//                   binding.progressMain.visibility = View.VISIBLE else binding.progressMain.visibility = View.GONE
//            }
//        }
    }

    private suspend fun observeSearch(){
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
                viewModel.setRoverSelection(rover)
            }
            val dateCallback = DateCallback {oldDate, newDate ->
                viewModel.setDateSelection(newDate)
            }
            val doneCallback = CompleteCallback { complete ->
                CoroutineScope(Dispatchers.IO).launch {
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