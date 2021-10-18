package com.mattg.rovergallery.network

import android.content.Context
import android.util.Log
import com.mattg.rovergallery.BuildConfig
import com.mattg.rovergallery.ManifestCallback
import com.mattg.rovergallery.R
import com.mattg.rovergallery.models.ManifestResponse
import com.mattg.rovergallery.models.ParameterResponse
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MarsApiCaller {
    private val BASE_URL = "https://api.nasa.gov/"
    private val MANIFEST_URL = "https://api.nasa.gov/mars-photos/api/v1/manifests/"

    private fun getApi(context: Context, type: Int): Retrofit {
        //   if(api == null){
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(logging)
        }
        //define and add a cache
        val cacheSize = 5 * 1024 * 1024L // = 5mb cache
        val cache =
            Cache(context.cacheDir, cacheSize) // the directory is obtained with context.cacheDir
        //add cache to client
        okHttpClient.cache(cache)
        //add custom interceptor
        okHttpClient.addInterceptor { chain ->
            //get the outgoing request
            val request = chain.request()
            //add another header to this request, it's been built but build a new one
            val newRequest = request.newBuilder() //can add headers below this
                .build()
            chain.proceed(newRequest)
        }
        val api = Retrofit.Builder()
            .baseUrl(if (type == 1) BASE_URL else MANIFEST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
        return api
    }


    /**
     * Will make a call for the given rovers manifest, where information about
     * amount of photos, when they were taken, and by what camera can be retrieved
     */
    fun getManifestForRover(context: Context, roverName: String, callback: ManifestCallback) {
        val manifest = getApi(context, 0).create(MarsApi::class.java).getBasicManifestForRover(
            roverName,
            context.resources.getString(R.string.api_key),
        ).enqueue(
            object : Callback<ManifestResponse> {
                override fun onResponse(
                    call: Call<ManifestResponse>,
                    response: Response<ManifestResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { callback.onData(it) }
                    }
                }

                override fun onFailure(call: Call<ManifestResponse>, t: Throwable) {
                    Log.e("APIERROR", "${t.message}")
                }

            }
        )
    }

    /**
     * Gets paginated results for a given rover on a given sol date
     */
    suspend fun getPhotosByRoverAndSol(
        context: Context,
        roverName: String,
        sol: Int,
        page: Int
    ): ParameterResponse {
        return getApi(context, 1).create(MarsApi::class.java).getPageByRoverAndSol(
            roverName,
            sol,
            page,
            context.resources.getString(R.string.api_key)
        )
    }

    /**
     * Gets paginated results for a given rover on a given earth date
     */
    suspend fun getPhotosByRoverAndEarthDate(
        context: Context,
        roverName: String,
        earthDate: String,
        page: Int
    ): ParameterResponse {
        return getApi(context, 1).create(MarsApi::class.java).getPageByRoverAndEarthDate(
            roverName,
            earthDate,
            page,
            context.resources.getString(R.string.api_key)
        )
    }

}