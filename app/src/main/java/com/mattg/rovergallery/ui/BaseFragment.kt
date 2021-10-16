package com.mattg.rovergallery.ui

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mattg.rovergallery.CompleteCallback
import com.mattg.rovergallery.DateCallback
import com.mattg.rovergallery.R
import com.mattg.rovergallery.RoverCallback
import com.mattg.rovergallery.databinding.DialogSelectionBinding


open class BaseFragment : Fragment() {

     var photoOptionsDialog: BottomSheetDialog? = null

    fun showBottomSheetDetailDialog(
        context: Context
    ) {

    }
    /**
     * Shows an options dialog that triggers api return changes.  Has a callback
     * to return results to trigger view model operations through repository
     */
    fun showBottomSheetSelectionDialog(
        context: Context,
        roverCallback: RoverCallback,
        dateCallback: DateCallback,
        completeCallback: CompleteCallback
    ) {
        val dialogBinding = DialogSelectionBinding.inflate(layoutInflater)
        photoOptionsDialog = BottomSheetDialog(context).apply {
            setContentView(dialogBinding.root)
            window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.rover_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerRover.adapter = adapter
        }

        dialogBinding.spinnerRover.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                roverCallback.optionSelected(parent?.getItemAtPosition(position) as String, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        dialogBinding.numberPicker.apply {
            minValue = 0
            maxValue = 1000 //TODO replace this value with actual available sols from manifest
            setOnValueChangedListener { picker, oldVal, newVal ->
            dateCallback.onDateSelected(
                oldVal,
                newVal
            )
        }
            dialogBinding.button.setOnClickListener {
                completeCallback.onComplete(complete = true)
            }

            photoOptionsDialog?.show()
        }


    }
    fun closeBottomSheet(){
        Log.d("EVENTCHECK", "close dialog called")
        if(photoOptionsDialog == null){
            return
        } else {
            photoOptionsDialog?.dismiss()
        }
    }

}