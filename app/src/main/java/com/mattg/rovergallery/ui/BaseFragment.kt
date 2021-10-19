package com.mattg.rovergallery.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.mattg.rovergallery.CompleteCallback
import com.mattg.rovergallery.ManifestCallback
import com.mattg.rovergallery.R
import com.mattg.rovergallery.databinding.DialogDatePickBinding
import com.mattg.rovergallery.databinding.DialogDetailBinding
import com.mattg.rovergallery.databinding.DialogSelectionBinding
import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.utils.fromCalenderSelection
import com.mattg.rovergallery.utils.toLocalDate
import com.mattg.rovergallery.viewModels.PhotosViewModel
import java.time.ZoneId
import android.view.View.OnTouchListener






open class BaseFragment : Fragment() {

     var roverOptionDialog: BottomSheetDialog? = null
     var dateOptionDialog: BottomSheetDialog? = null
     var photoDetailsDialog: BottomSheetDialog? = null

    //binding variables for various bottom sheet dialogs
    private lateinit var roverSelectBinding: DialogSelectionBinding
    private lateinit var dateSelectBinding: DialogDatePickBinding

    private lateinit var numberPicker: NumberPicker

    private lateinit var viewModel: PhotosViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(PhotosViewModel::class.java)
    }

    /**
     * Displays the rover manifest (accessed from the photo details screen)
     */
    fun showBottomSheetDetailDialog(
        context: Context,
        manifestResponse: ManifestResponse
    ) {
        val detailBinding: DialogDetailBinding = DialogDetailBinding.inflate(layoutInflater).apply {
            manifest = manifestResponse
        }
        photoDetailsDialog = BottomSheetDialog(context).apply {
            setContentView(detailBinding.root)
            window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            detailBinding.btnDone.setOnClickListener {
               this.dismiss()
            }
        }
        photoDetailsDialog?.show()
    }
    /**
     * Shows an options dialog that triggers api return changes.  Has a callback
     * to return results to trigger view model operations through repository
     */
    @SuppressLint("ClickableViewAccessibility")
    fun showRoverSelectDialog(
        context: Context,
        doneCallback: CompleteCallback?
    ) {

            if(isOnline(requireContext())) {
                roverSelectBinding = DialogSelectionBinding.inflate(layoutInflater)
                roverSelectBinding.viewModel = viewModel
                roverOptionDialog = BottomSheetDialog(context).apply {
                    setContentView(roverSelectBinding.root)
                    window?.setLayout(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                roverSelectBinding.radioRover.setOnCheckedChangeListener { group, checkedId ->
                    when(checkedId){
                        R.id.rbCuriosity -> {
                            viewModel.setRoverSelection("Curiosity")
                        }
                        R.id.rbOpportunity -> {
                            viewModel.setRoverSelection("Opportunity")
                        }
                        R.id.rbSpirit -> {
                            viewModel.setRoverSelection("Spirit")
                        }
                    }
                }


                roverSelectBinding.btnSelectRover.setOnClickListener {
                    roverOptionDialog?.dismiss()
                    showDateSelectDialog(context, doneCallback)
                }
                roverOptionDialog?.show()

            } else {
                Toast.makeText(requireContext(), "No internet connection detected", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Displays a material date selector based on available rover photo dates
     */
    fun showDateSelectDialog(
        context: Context,
        doneCallback: CompleteCallback?
    ) {
        if(isOnline(requireContext())) {
            Toast.makeText(context, "Getting dates..", Toast.LENGTH_LONG).show()
            Thread.sleep(2000)
            val end = viewModel.manifestResponse.value?.peekContent()?.photo_manifest?.max_date
            val start = viewModel.manifestResponse.value?.peekContent()?.photo_manifest?.landing_date
            val builder = MaterialDatePicker.Builder.datePicker().apply {
                setCalendarConstraints(
                    CalendarConstraints.Builder().setStart(
                        start?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli()
                    )
                        .setEnd(
                            end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                .toEpochMilli()
                        )
                        .setOpenAt(
                            end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                .toEpochMilli()
                        )
                        .build()
                )
                setTitleText("Select Mission Date")
                setSelection(
                    end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli()
                )
            }
            val picker = builder.build()

            picker.show(childFragmentManager, picker.toString())
            picker.clearOnPositiveButtonClickListeners()
            picker.apply {

                addOnPositiveButtonClickListener {
                    this.dismiss()
                    if (selection!! > end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault())
                            .toInstant().toEpochMilli()
                    ) {
                        AlertDialog.Builder(requireContext()).setTitle("Invalid Date Selection")
                            .setMessage("Last date for available photos from ${viewModel._roverChoice.value} is $end")
                            .setPositiveButton(
                                "Choose New Date"
                            ) { _, _ -> showDateSelectDialog(context, doneCallback) }
                            .setNegativeButton("Cancel") { _, _ ->
                                picker.dismiss()
                                viewModel.lastRover
                                dismiss()
                            }
                            .show()
                    } else {

                        selection?.fromCalenderSelection()?.let { it1 ->
                            viewModel.isEarthDateSearch = true
                            viewModel.setEarthDateSelection(
                                it1
                            )

                        }
                        doneCallback?.onComplete(true)
                    }
                }

            }
        }else {
            Toast.makeText(requireContext(), "No internet connection detected", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Will get results from api by sol
     */
    fun showSolSelectDialog(
        context: Context,
        doneCallback: CompleteCallback?
    ) {
        dateSelectBinding = DialogDatePickBinding.inflate(layoutInflater)
        dateOptionDialog = BottomSheetDialog(context).apply {
            setContentView(dateSelectBinding.root)
            window?.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            dateSelectBinding.numberPicker.minValue = 0
            dateSelectBinding.numberPicker.maxValue =
                if (viewModel.manifestResponse.value?.peekContent()?.photo_manifest?.max_sol == null)
                    1000 else viewModel.manifestResponse.value?.peekContent()?.photo_manifest?.max_sol!!

            dateSelectBinding.numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                viewModel.setDateSelection(
                    newVal
                )
            }

            dateSelectBinding.btnSelectDate.setOnClickListener {
                doneCallback?.onComplete(true)
                dateOptionDialog?.dismiss()
            }
            dateOptionDialog?.show()
        }

    }

    fun closeBottomSheet(){
        if(roverOptionDialog == null){
            return
        } else {
            roverOptionDialog?.dismiss()
        }
    }

    /**
     * Checks for connection before attempting to search photos
     */
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}

