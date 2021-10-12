package com.mattg.rovergallery.repositories

import android.app.Application
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.network.MarsApiCaller
import com.mattg.rovergallery.utils.RoverName

class PhotosRepository (private val application: Application){
    private val api = MarsApiCaller

    fun getPhotosFromApi(string: String, pageIndex: Int): ParameterResponse {
       return MarsApiCaller.getPhotosByRover(application, RoverName.ROVER_CURIOSITY, pageIndex)
    }

    fun getSearchedPhotosFromApiByRover(string: String, roverName: RoverName, pageIndex: Int): ParameterResponse {
        return MarsApiCaller.getPhotosByRover(application, roverName, pageIndex)
    }

    fun getSearchedPhotosFromApiByRover(string: String, roverName: RoverName, sol: Int, pageIndex: Int): ParameterResponse {
        return MarsApiCaller.getPhotosByRover(application, roverName, pageIndex)
    }

    fun updatePhotosFromApi() {

    }


}