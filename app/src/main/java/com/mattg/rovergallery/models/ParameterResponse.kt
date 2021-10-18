package com.mattg.rovergallery.models

/**
 * Basic classes to represent the API return results of a specific request for
 * photos
 */

class Camera {
    var id = 0
    var name: String? = null
    var rover_id = 0
    var full_name: String? = null

    fun titleName(): String = "Camera: $full_name"
}

class Rover {
    var id = 0
    var name: String? = null
    var landing_date: String? = null
    var launch_date: String? = null
    var status: String? = null
    fun titleName(): String = "Rover Name: $name"
}


class ParameterResponse {
    var photos: List<Photo>? = null
}