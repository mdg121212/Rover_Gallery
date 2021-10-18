package com.mattg.rovergallery

import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.models.Photo

/**
 * Callback classes for options menu to communicate with view model
 */
class CompleteCallback(val complete : (complete: Boolean) -> Unit) {
    fun onComplete(complete: Boolean) = complete(complete)
}

class RecyclerCallback(val clicked: (photo: Photo, position: Int) -> Unit) {
    fun photoClicked(photo: Photo, position: Int) = clicked(photo, position)
}
class ManifestCallback(var onData: (data: ManifestResponse) -> Unit) {
    fun onResponse(data: ManifestResponse) = onData(data)
}