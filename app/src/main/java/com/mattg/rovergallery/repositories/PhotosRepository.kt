package com.mattg.rovergallery.repositories

import android.app.Application
import android.util.Log
import com.mattg.rovergallery.ManifestCallback
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.network.MarsApiCaller
import com.mattg.rovergallery.network.PhotosPagingSource

/**
 * Repository class to handle interaction with photo clas
 */
class PhotosRepository(private val application: Application) {
    private val api = MarsApiCaller

    suspend fun getSearchedPhotosFromApiByRover(
        string: String,
        roverName: String,
        sol: Int,
        pageIndex: Int,
    ): ParameterResponse {
        return MarsApiCaller.getPhotosByRoverAndSol(application, roverName, sol, pageIndex)
    }

    suspend fun getSearchedPhotosFromApiByRoverEarthDate(
        string: String,
        roverName: String,
        date: String,
        pageIndex: Int,
    ): ParameterResponse {
        return MarsApiCaller.getPhotosByRoverAndEarthDate(application, roverName, date, pageIndex)
    }

    /**
     * Returns a paging source based on input via retrofit
     */
    fun getPagedData(application: Application, roverName: String, sol: Int): PhotosPagingSource {
        val data =
            PhotosPagingSource(
                application,
                this,
                sol,
                null,
                roverName,
            )
        return data;
    }

    /**
     * Returns a paging source based on input via retrofit for earth date selection
     */
    fun getPagedDataEarthDate(
        application: Application,
        roverName: String,
        date: String
    ): PhotosPagingSource {
        val data =
            PhotosPagingSource(
                application,
                this,
                null,
                date,
                roverName,
            )
        return data;
    }

    /**
     * Submits a request for a rovers manifest, the response is returned through the
     * callback passed in as a parameter.
     */
    fun getManifest(application: Application, roverName: String, callback: ManifestCallback) {
        MarsApiCaller.getManifestForRover(
            application,
            roverName = roverName,
            callback
        )
    }

}
