package com.mattg.rovergallery.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mattg.rovergallery.Event
import com.mattg.rovergallery.R
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.network.PhotosPagingSource
import com.mattg.rovergallery.repositories.PhotosRepository
import com.mattg.rovergallery.utils.RoverCameras
import com.mattg.rovergallery.utils.RoverName

class PhotosViewModel(app: Application) : AndroidViewModel(app) {

    private val photoRepo = PhotosRepository(app)

    private val _roverChoice = MutableLiveData<RoverName>()
    private val _solChoice = MutableLiveData<Int>()
    private val _cameraChoice = MutableLiveData<RoverCameras>()
    private val _showLoadingSpinner = MutableLiveData<Event<Boolean>>()
    private val _photosList = MutableLiveData<List<Photo>>()
    val flow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 25)
    ) {
        PhotosPagingSource(app, photoRepo)
    }.flow
        .cachedIn(viewModelScope)
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

}