package com.mattg.rovergallery.models

/**
 * Classes to represent the return of a Manifest check API call.
 * This call can return the number of available photos, sols, and other
 * useful information for more specific queries.
 */
class ManifestPhoto {
    var sol = 0
    var earth_date: String? = null
    var total_photos = 0
    var cameras: List<String>? = null

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}

class PhotoManifest {
    var name: String? = null
    var landing_date: String? = null
    var launch_date: String? = null
    var status: String? = null
    var max_sol = 0
    var max_date: String? = null
    var total_photos = 0
    var photos: List<ManifestPhoto>? = null
}

class ManifestResponse {
    var photo_manifest: PhotoManifest? = null
}