package com.mattg.rovergallery.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mattg.rovergallery.DataCallback
import com.mattg.rovergallery.utils.Event
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.network.PhotosPagingSource
import com.mattg.rovergallery.repositories.PhotosRepository
import com.mattg.rovergallery.utils.RoverCameras
import com.mattg.rovergallery.utils.RoverName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.util.concurrent.Flow

class PhotosViewModel(app: Application) : AndroidViewModel(app) {
    //https://proandroiddev.com/android-singleliveevent-redux-with-kotlin-flow-b755c70bb055
    sealed class Event {
        object CloseDialog: Event()
        object ToggleProgress: Event()
        data class ShowSnackBar(val text: String): Event()
        data class ShowToast(val text: String): Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val photoRepo = PhotosRepository(app)
    private val _cameraChoice = MutableLiveData<RoverCameras>()

    private val _pagingData = MutableLiveData<PagingData<Photo>>()
    //public so that they can be data bound
    val _roverChoice = MutableLiveData<String>("Curiosity")
    val _solChoice = MutableLiveData<Int>(1000)
    var _selectedPhoto = MutableLiveData<Photo>()


    /**
     * callback to handle no result case
     */
    private var callback = DataCallback {
        if (it) {
            Log.d("CALLBACKTEST", "GOT DATA")
            viewModelScope.launch {
                eventChannel.send(Event.CloseDialog)
                eventChannel.send(Event.ToggleProgress)
            }

        } else {
            Log.d("CALLBACKTEST", "GOT NO DATA")
            viewModelScope.launch {
                eventChannel.send(Event.ShowToast("No photos found for that sol :("))
            } }
        }


    var flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 25)
    ) {
        PhotosPagingSource(app, photoRepo, _solChoice.value, _roverChoice.value, callback)
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
                    callback
                )
            }
        }
        fromRepo?.let {
            Log.d("PageTrack", "from repo was not null!! getting new data?")
            flow = Pager(
                PagingConfig(pageSize = 25)
            ) {
                //PhotosPagingSource(getApplication(), photoRepo, sol, rover)
                  it
            }.flow.cachedIn(viewModelScope)
            Log.d("PageTrack", "returning newly retrieved flow")
            return flow
        }
        //return current data if not changed
        Log.d("PageTrack", "returning old flow")
        return flow;
    }

    /**
     * Gets an initial bit of data for the homescreen prior to search
     * parameters being chosen
     */
    fun getInitialApiData() {

    }

    /**
     * Gets more specific data for the home screen, once a set of seach paramters has
     * been chosen
     */
    fun getSearchValueApiData(){

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

}

/**
 * Classes borrowed from Google Paging Example, modified to use
 */
private val UiAction.Scroll.shouldFetchMore
    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction()
}

data class UiState(
    val query: String,
    val searchResult: ParameterResponse
)

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Curiosity"