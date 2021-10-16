package com.mattg.rovergallery

import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.models.Photo

/**
 * Callback classes for options menu to communicate with view model
 */
class RoverCallback(val optionSelected : (optionRover: String, position: Int) -> Unit) {
    fun onRoverSelected(optionRover: String, position: Int)
    = optionSelected(optionRover, position)
}

class DateCallback(val optionSelected : (option: Int, position: Int) -> Unit) {
    fun onDateSelected(option: Int, position: Int)
            = optionSelected(option, position)
}

class CompleteCallback(val complete : (complete: Boolean) -> Unit) {
    fun onComplete(complete: Boolean) = complete(complete)
}

class RecyclerCallback(val clicked: (photo: Photo, position: Int) -> Unit) {
    fun photoClicked(photo: Photo, position: Int) = clicked(photo, position)
}

class DataCallback(var onData: (wasData: Boolean) -> Unit) {
    fun wasData(wasData: Boolean) = onData(wasData)
}

class ManifestCallback(var onData: (data: ManifestResponse) -> Unit) {
    fun onResponse(data: ManifestResponse) = onData(data)
}