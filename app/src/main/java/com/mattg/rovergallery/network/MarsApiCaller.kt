package com.mattg.rovergallery.network

import android.content.Context
import com.mattg.rovergallery.BuildConfig
import com.mattg.rovergallery.R
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.utils.RoverCameras
import com.mattg.rovergallery.utils.RoverName
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object MarsApiCaller {
    private val BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/"
    private val MANIFEST_URL = "https://api.nasa.gov/mars-photos/api/v1/manifests/"

    //curiosity/photos?sol=1000&api_key=DEMO_KEY"
    //Example queries
    //
    //https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY
    //
    //https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&camera=fhaz&api_key=DEMO_KEY
    //
    //https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&page=2&api_key=DEMO_KEY

    private fun getApi(context: Context, type: Int) : Retrofit {
        //   if(api == null){
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        if(BuildConfig.DEBUG){
            okHttpClient.addInterceptor(logging)
        }
        //define and add a cache
        val cacheSize = 5 * 1024 * 1024L // = 5mb cache
        val cache = Cache(context.cacheDir, cacheSize) // the directory is obtained with context.cacheDir
        //add cache to client
        okHttpClient.cache(cache)
        //add custom interceptor
        okHttpClient.addInterceptor{ chain ->
            //get the outgoing request
            val request = chain.request()
            //add another header to this request, it's been built but build a new one
            val newRequest = request.newBuilder() //can add headers below this
                .build()
            chain.proceed(newRequest)
        }
        val api = Retrofit.Builder()
            .baseUrl(if(type == 1) BASE_URL else MANIFEST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
        return api
    }

    /**
     * Private function to return the pre-req for a photos call
     */
     private fun marsPhotoApi(context: Context) : MarsApi {
        return getApi(context, 1).create(MarsApi::class.java)
    }

    /**
     * Private function to return the pre-req for a manifest call
     */
    private fun marsManifestApi(context: Context) : MarsApi {
        return getApi(context, 0).create(MarsApi::class.java)
    }

    /**
     * Will make a call for the given rovers manifest, where information about
     * amount of photos, when they were taken, and by what camera can be retrieved
     */
    fun getManifestForRover(context: Context, roverName: RoverName) {

    }

    /**
     * Returns an api call for photos by rover ready to enqueue
     */
    suspend fun getPhotosByRover(context: Context, roverName: RoverName, page: Int): ParameterResponse {
        return getApi(context, 1).create(MarsApi::class.java).getDefault(
            page,
            context.resources.getString(R.string.api_key)
        )
    }

    /**
     * Returns an api call for photos by rover ready to enqueue
     */
    fun getPhotosByRoverAndCamera(context: Context, roverName: RoverName, cameraList: List<RoverCameras>) {

    }
}