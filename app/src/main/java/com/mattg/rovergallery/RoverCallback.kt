package com.mattg.rovergallery

import android.graphics.Bitmap
import com.mattg.rovergallery.utils.RoverName

/**
 * Callback classes for options menu to communicate with view model
 */
class RoverCallback(val optionSelected : (optionRover: String, position: Int) -> Unit){
    fun onRoverSelected(optionRover: String, position: Int)
    = optionSelected(optionRover, position)
}

class DateCallback(val optionSelected : (option: Int, position: Int) -> Unit){
    fun onDateSelected(option: Int, position: Int)
            = optionSelected(option, position)
}

class CompleteCallback(val complete : (complete: Boolean) -> Unit){
    fun onComplete(complete: Boolean) = complete(complete)
}