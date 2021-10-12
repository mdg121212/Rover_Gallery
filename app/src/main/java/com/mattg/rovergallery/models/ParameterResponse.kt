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
}

class Rover {
    var id = 0
    var name: String? = null
    var landing_date: String? = null
    var launch_date: String? = null
    var status: String? = null
}


class ParameterResponse {
    var photos: List<Photo>? = null
}