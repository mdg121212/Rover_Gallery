package com.mattg.rovergallery.utils

/**
 * Enum class to hold rover name values to search by
 * Curiosity, Opportunity and Spirit
 */
enum class RoverName(val string: String, val cameras: Array<RoverCameras>) {
    ROVER_CURIOSITY("curiosity", arrayOf(
        RoverCameras.CAMERA_FHAZ, RoverCameras.CAMERA_MAHLI,
        RoverCameras.CAMERA_MARDI, RoverCameras.CAMERA_MAST,
        RoverCameras.CAMERA_CHEM_CAM, RoverCameras.CAMERA_RHAZ,
        RoverCameras.CAMERA_NAVCAM
    )),
    ROVER_OPPORTUNITY("opportunity", arrayOf(
        RoverCameras.CAMERA_FHAZ, RoverCameras.CAMERA_RHAZ,
        RoverCameras.CAMERA_NAVCAM, RoverCameras.CAMERA_PAN_CAM,
        RoverCameras.CAMERA_MINTIES
    )),
    ROVER_SPIRIT("spirit", arrayOf(
        RoverCameras.CAMERA_FHAZ, RoverCameras.CAMERA_RHAZ,
        RoverCameras.CAMERA_NAVCAM, RoverCameras.CAMERA_PAN_CAM,
        RoverCameras.CAMERA_MINTIES
    ))
}