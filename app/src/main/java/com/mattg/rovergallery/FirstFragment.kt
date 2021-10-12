package com.mattg.rovergallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mattg.rovergallery.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    /**
     * Observe live data variables
     */
    private fun observeViewModel() {

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}