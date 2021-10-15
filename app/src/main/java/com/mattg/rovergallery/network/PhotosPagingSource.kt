package com.mattg.rovergallery.network

import android.app.Application
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mattg.rovergallery.DataCallback
import com.mattg.rovergallery.R
import com.mattg.rovergallery.models.ParameterResponse
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.repositories.PhotosRepository
import retrofit2.HttpException
import java.io.IOException


private const val NASA_STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 25

/**
 * Paging source adapted from google documentation and codelabs to make retrofit call, and return
 * paginated results to the ui layer
 */
class PhotosPagingSource(
    val application: Application,
    private val photoRepo: PhotosRepository,
    private val sol: Int?,
    private val rover: String?,//to handle the network calls
    val callback: DataCallback

) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        //either load via parameter value or by default if non existent
        val pageIndex = params.key ?: NASA_STARTING_PAGE_INDEX

        return try {
            var response: ParameterResponse
            if(rover != null && sol != null) {
                Log.d("PageTrack", "getting updated data with rover: $rover and sol: $sol")
                response = photoRepo.getSearchedPhotosFromApiByRover(
                    application.resources.getString(R.string.api_key),
                    rover,
                    sol,
                    pageIndex
                )
            } else {
                Log.d("DATATEST", "getting standard data")
                response = photoRepo.getSearchedPhotosFromApiByRover(
                    application.resources.getString(R.string.api_key),
                    "Curiosity",
                    1000,
                    pageIndex
                )
            }

            val photos = response.photos!!
            if(photos.isEmpty()){
                callback.wasData(false)
            } else {
                callback.wasData(true)
            }
            Log.d("DATATEST", "GOT PHOTOS ${photos.size}")
            val nextKey =
                if (photos.isEmpty()) {
                    null
                } else {
                    pageIndex + 1
                }
            LoadResult.Page(
                data = photos,
                prevKey = if (pageIndex == NASA_STARTING_PAGE_INDEX) null else pageIndex,
                nextKey = nextKey
            )

        }catch (e: IOException) {
            Log.d("DATATESTERROR", e.toString())
            callback.wasData(false)
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("DATATESTERROR", e.toString())
            callback.wasData(false)
            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        }

    }

}