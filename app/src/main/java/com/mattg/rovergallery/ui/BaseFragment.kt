package com.mattg.rovergallery.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.mattg.rovergallery.CompleteCallback
import com.mattg.rovergallery.R
import com.mattg.rovergallery.databinding.DialogDatePickBinding
import com.mattg.rovergallery.databinding.DialogDetailBinding
import com.mattg.rovergallery.databinding.DialogSelectionBinding
import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.utils.fromCalenderSelection
import com.mattg.rovergallery.utils.toLocalDate
import com.mattg.rovergallery.viewModels.PhotosViewModel
import java.time.ZoneId



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
    fun showRoverSelectDialog(
        context: Context,
        doneCallback: CompleteCallback?
    ) {

            roverSelectBinding = DialogSelectionBinding.inflate(layoutInflater)
            roverSelectBinding.viewModel = viewModel
            roverOptionDialog = BottomSheetDialog(context).apply {
                setContentView(roverSelectBinding.root)
                window?.setLayout(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
            }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.rover_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roverSelectBinding.spinnerRover.adapter = adapter
        }
        var isFirst = true;

        roverSelectBinding.spinnerRover.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(isFirst){
                        isFirst = false
                        return
                    }
                    viewModel.setRoverSelection(parent?.getItemAtPosition(position) as String)
                    return
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            roverSelectBinding.btnSelectRover.setOnClickListener {
                roverOptionDialog?.dismiss()
                showDateSelectDialog(context, doneCallback)
            }
            roverOptionDialog?.show()


    }

    /**
     * Displays a material date selector based on available rover photo dates
     */
    fun showDateSelectDialog(
        context: Context,
        doneCallback: CompleteCallback?
    ) {
            val end = viewModel.manifestResponse.value?.peekContent()?.photo_manifest?.max_date
            val start = viewModel.manifestResponse.value?.peekContent()?.photo_manifest?.landing_date
            val builder = MaterialDatePicker.Builder.datePicker().apply {
                setCalendarConstraints(
                        CalendarConstraints.Builder().setStart(start?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .setEnd(end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .setOpenAt(end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .build()
                )
                setTitleText("Select Mission Date")
                setSelection(end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            }
            val picker = builder.build()

            picker.show(childFragmentManager, picker.toString())
            picker.clearOnPositiveButtonClickListeners()
            picker.apply {

                addOnPositiveButtonClickListener {
                    this.dismiss()
                    if(selection!! > end?.toLocalDate()!!.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                        AlertDialog.Builder(requireContext()).setTitle("Invalid Date Selection")
                            .setMessage("Last date for available photos from ${viewModel._roverChoice.value} is $end")
                            .setPositiveButton("Choose New Date"
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

}

