package com.mattg.rovergallery.network

import android.app.Application
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mattg.rovergallery.R
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
    private val photoRepo: PhotosRepository //to handle the network call
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
            val response = photoRepo.getPhotosFromApi(
                application.resources.getString(R.string.api_key),
                pageIndex
            )

            val photos = response.photos!!
            Log.d("DATATEST", "GOT PHOTOS ${photos.size}")
            val nextKey =
                if (photos.isEmpty()) {
                    null
                } else {
                    params.loadSize / NETWORK_PAGE_SIZE
                }
            LoadResult.Page(
                data = photos,
                prevKey = if (pageIndex == NASA_STARTING_PAGE_INDEX) null else pageIndex,
                nextKey = nextKey
            )
        }catch (e: IOException) {
            Log.d("DATATEST", e.toString())
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("DATATEST", e.toString())

            // HttpException for any non-2xx HTTP status codes.
            return LoadResult.Error(e)
        }

    }

}