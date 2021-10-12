package com.mattg.rovergallery

/**
 * Interface to handle observation of live data events
 */
interface Observer<T> {
    fun onUpdate(data: T?) : T? {
        return data }
}