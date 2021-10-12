package com.mattg.rovergallery.utils

/**
 * Enum class to hold the values of available cameras
 *
  FHAZ	Front Hazard Avoidance Camera	✔	✔	✔
  RHAZ	Rear Hazard Avoidance Camera	✔	✔	✔
  MAST	Mast Camera	✔
  CHEMCAM	Chemistry and Camera Complex	✔
  MAHLI	Mars Hand Lens Imager	✔
  MARDI	Mars Descent Imager	✔
  NAVCAM	Navigation Camera	✔	✔	✔
  PANCAM	Panoramic Camera		✔	✔
  MINITES	Miniature Thermal Emission Spectrometer (Mini-TES)		✔	✔
 *
 */
enum class RoverCameras(val string: String) {
    CAMERA_FHAZ("fhaz"),
    CAMERA_RHAZ("rhaz"),
    CAMERA_MAST("mast"),
    CAMERA_CHEM_CAM("chemcam"),
    CAMERA_MAHLI("mahli"),
    CAMERA_MARDI("mardi"),
    CAMERA_NAVCAM("navcam"),
    CAMERA_MINTIES("minties"),
    CAMERA_PAN_CAM("pancam")
}