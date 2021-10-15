package com.mattg.rovergallery.network

import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.models.ParameterResponse
import retrofit2.Call
import retrofit2.http.*

interface MarsApi {

    /**
     * Returns the default page suggested by this api but allows for
     * changing of the rover
     */
    @GET("/{rover}/photos?sol=1000")
    fun getPageByRover(
                       @Path("rover") rover: String,
                       @Query("page") page: Int,
                       @Query("api_key") key: String) : Call<ParameterResponse>

    @GET("mars-photos/api/v1/rovers/{name}/photos")
    suspend fun getPageByRoverAndSol(
        @Path("name") rover: String,
        @Query("sol") sol: Int,
        @Query("page") page: Int,
        @Query("api_key") key: String) : ParameterResponse

    /**
     * Gets the photos from a given rover at a given time
     */
    @GET("/{rover}/photos?")
    fun getSpecificPhotoSet(
                       @Path("rover") rover: String,
                       @Query("page") page: Int,
                       @Query("sol") sol: Int,
                       @Query("camera") camera: String,
                       @Query("api_key") key: String) : Call<ParameterResponse>

    /**
     * Gets a manifest return for a given rover.  This provides information like
     * how many sols are available, and / or pictures taken per sol
     */
    @GET("{name}?")
    fun getBasicManifestForRover(
        @Path("name") roverName: String,
        @Query("api_key") key: String
    ) : Call<ManifestResponse>


}