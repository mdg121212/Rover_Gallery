package com.mattg.rovergallery.viewModels

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mattg.rovergallery.DataCallback
import com.mattg.rovergallery.ManifestCallback
import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.utils.Event
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.network.PhotosPagingSource
import com.mattg.rovergallery.repositories.PhotosRepository
import com.mattg.rovergallery.utils.RoverCameras
import com.mattg.rovergallery.utils.RoverName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.util.concurrent.Flow

class PhotosViewModel(app: Application) : AndroidViewModel(app) {

//    sealed class Event {
//        object CloseDialog: Event()
//        object ToggleProgress: Event()
//        data class ShowSnackBar(val text: String): Event()
//        data class ShowToast(val text: String): Event()
//    }
//
//    private val eventChannel = Channel<Event>(Channel.BUFFERED)
//    val eventsFlow = eventChannel.receiveAsFlow()


    private val photoRepo = PhotosRepository(app)
    //public so that they can be data bound
    val _roverChoice = MutableLiveData<String>("Curiosity")
    val _solChoice = MutableLiveData<Int>(1000)
    val _solChoiceString = MutableLiveData<String>("1000")
    var _selectedPhoto = MutableLiveData<Photo>()
    var _manifestResponse = MutableLiveData<ManifestResponse>()
    val manifestResponse: LiveData<ManifestResponse> get() = _manifestResponse

    private val _toastEvent = MutableLiveData<Event<String>>()
    private val _spinnerEvent = MutableLiveData<Event<Int>>()
    private val _dialogEvent = MutableLiveData<Event<Int>>()
    val toastEvent: LiveData<Event<String>> get() = _toastEvent
    val spinnerEvent: LiveData<Event<Int>> get() = _spinnerEvent
    val dialogEvent: LiveData<Event<Int>> get() = _dialogEvent

    //initialize the flow variable with default parameters
    var flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 25)
    ) {
        PhotosPagingSource(app, photoRepo, _solChoice.value, _roverChoice.value)
    }.flow.cachedIn(viewModelScope)

    /**
     * Resets the flow data
     */
    @JvmName("getFlow1")
    fun getFlow(): kotlinx.coroutines.flow.Flow<PagingData<Photo>>? {
        val fromRepo = _solChoice.value?.let {
            _roverChoice.value?.let { it1 ->
                Log.d("PageTrack", "searching for sol: $it and rover: $it1")
                photoRepo.getPagedData(
                    getApplication(),
                    it1,
                    it,
                )
            }
        }
        fromRepo?.let {
            Log.d("PageTrack", "from repo was not null!! getting new data?")
            flow = Pager(
                PagingConfig(pageSize = 25)
            ) {
                  it
            }.flow.cachedIn(viewModelScope)
            Log.d("PageTrack", "returning newly retrieved flow")
            toggleSpinner()
            return flow
        }
        //return current data if not changed
        Log.d("PageTrack", "returning old flow")
        return flow;
    }

    /**
     * Sets the selected photo for the photo fragment
     */
    fun setPhoto(photo: Photo) {
        _selectedPhoto.postValue(photo)
    }

    fun setRoverSelection(rover: String) {
        _roverChoice.postValue(rover)
    }

    fun setDateSelection(newDate: Int) {
        _solChoice.postValue(newDate)
    }

    /**
     * Event trigger for view
     */
    fun closeDialog() {
        Log.d("EVENTCHECK", "close dialog posting event")
        _dialogEvent.postValue(Event(0))
    }

    fun toggleSpinner() {
        Log.d("EVENTCHECK", "toggle spinner posting event")
        _spinnerEvent.postValue(Event(0))
    }

    /**
     * Gets the manifest data for a chosen rover (for detail fragments bottom sheet info view)
     */
    fun getManifest() {
        _roverChoice.value?.let {
            photoRepo.getManifest(
                getApplication(),
                it,
                ManifestCallback(){ response ->
                _manifestResponse.postValue(response)
                }
            )
        }
    }

    object bindingObject{
        @JvmStatic
        @BindingAdapter("android:text")
        fun setIntText(textView: TextView, number: Int) {
            textView.text = number.toString()
        }
    }
}
