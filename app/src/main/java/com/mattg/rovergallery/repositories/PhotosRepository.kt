package com.mattg.rovergallery.repositories

import android.app.Application
import android.util.Log
import com.mattg.rovergallery.DataCallback
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.network.MarsApiCaller
import com.mattg.rovergallery.network.PhotosPagingSource
import com.mattg.rovergallery.utils.RoverName

/**
 * Repository class to handle interaction with photo clas
 */
class PhotosRepository (private val application: Application){
    private val api = MarsApiCaller

    suspend fun getSearchedPhotosFromApiByRover(string: String, roverName: String, sol: Int, pageIndex: Int): ParameterResponse {
        return MarsApiCaller.getPhotosByRoverAndSol(application, roverName, sol, pageIndex)
    }

    /**
     * Returns a paging source based on input via retrofit
     */
    fun getPagedData(application: Application, roverName: String, sol: Int, callback: DataCallback): PhotosPagingSource {
        Log.d("PageTrack: ", "should be returning new data with params name: $roverName and sol:$sol")
        val data = PhotosPagingSource(
            application,
            this,
            sol,
            roverName,
            callback
        )
        Log.d("PageTrack: ", "new object reference is : $data")
        return data;
    }


}
