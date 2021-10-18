package com.mattg.rovergallery.viewModels

import android.app.Application
import android.util.Log
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mattg.rovergallery.ManifestCallback
import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.network.PhotosPagingSource
import com.mattg.rovergallery.repositories.PhotosRepository
import com.mattg.rovergallery.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(app: Application) : AndroidViewModel(app) {

    private val photoRepo = PhotosRepository(app)

    //public so that they can be data bound
    val _roverChoice = MutableLiveData<String>()
    val _solChoice = MutableLiveData<Int>()
    val _earthDateChoice = MutableLiveData<String>()
    val _solChoiceString = MutableLiveData<String>()
    var _selectedPhoto = MutableLiveData<Photo>()
    var _manifestResponse = MutableLiveData<Event<ManifestResponse>>()
    val manifestResponse: LiveData<Event<ManifestResponse>> get() = _manifestResponse

    private val _toastEvent = MutableLiveData<Event<String>>()
    private val _spinnerEvent = MutableLiveData<Event<Int>>()
    private val _dialogEvent = MutableLiveData<Event<Int>>()
    val toastEvent: LiveData<Event<String>> get() = _toastEvent
    val spinnerEvent: LiveData<Event<Int>> get() = _spinnerEvent
    val dialogEvent: LiveData<Event<Int>> get() = _dialogEvent
    var lastRover: String = ""
    var isEarthDateSearch = false;

    init {
        setRoverSelection("Curiosity")
        setDateSelection(1000)
        setEarthDateSelection("2015-05-30")
    }

    //initialize the flow variable with default parameters
    var flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 25)
    ) {
        PhotosPagingSource(app, photoRepo, _solChoice.value, _earthDateChoice.value, null)
    }.flow.cachedIn(viewModelScope)

    /**
     * Resets the flow data
     */
    @JvmName("getFlow1")
    fun getFlow(): kotlinx.coroutines.flow.Flow<PagingData<Photo>> {

        val fromRepo = when (isEarthDateSearch) {
            true -> {
                _earthDateChoice.value?.let {
                    _roverChoice.value?.let { it1 ->
                        Log.d("PageTrack", "searching for sol: $it and rover: $it1")
                        photoRepo.getPagedDataEarthDate(
                            getApplication(),
                            it1,
                            it,
                        )
                    }
                }
            }
            false -> {
                _solChoice.value?.let {
                    _roverChoice.value?.let { it1 ->
                        Log.d("PageTrack", "searching for sol: $it and rover: $it1")
                        photoRepo.getPagedData(
                            getApplication(),
                            it1,
                            it,
                        )
                    }
                }
            }
        }

        try {
            fromRepo?.let {
                flow = Pager(
                    PagingConfig(pageSize = 25)
                ) {
                    it
                }.flow.cachedIn(viewModelScope)
                toggleSpinner()
                return flow
            }
        } catch (e: Throwable) {
            Log.e("FlowError", "${e.message}")
            e.printStackTrace()
        }
        //return current data if not changed
        return flow;
    }

    /**
     * Sets the selected photo for the photo fragment
     */
    fun setPhoto(photo: Photo) {
        _selectedPhoto.postValue(photo)
    }

    /**
     * Sets the rover name for display and use in api calls
     */
    fun setRoverSelection(rover: String) {
        lastRover = _roverChoice.value.toString()
        _roverChoice.value = (rover)
        getManifest(rover)
    }

    /**
     * In the event of a cancelled search flow, this will reset values as far as the ui is
     * concerned
     */
    fun lastRover() {
        _roverChoice.value = lastRover
    }

    /**
     * Sets the martial sol selection for display and use in api calls
     */
    fun setDateSelection(newDate: Int) {
        _solChoice.value = (newDate)
        _solChoiceString.value = newDate.toString()
    }

    fun setSol(newSol: Int) {
        _solChoice.postValue(newSol)
        _solChoiceString.postValue(newSol.toString())
    }

    /**
     * Sets the earth date string for display and use in api calls
     */
    fun setEarthDateSelection(dateString: String) {
        _earthDateChoice.value = dateString
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
     * and sets a live data backing variable to this response
     */
    fun getManifest(rover: String) {
        Log.d("MANIFESTY", "GETTING MANIFEST FOR $rover")
        photoRepo.getManifest(
            getApplication(),
            rover,
            ManifestCallback() { response ->
                _manifestResponse.value = (Event(response))
            }
        )
    }
    object bindingObject {
        @JvmStatic
        @BindingAdapter("android:text")
        fun setIntText(textView: TextView, number: Int) {
            textView.text = number.toString()
        }
    }
}
