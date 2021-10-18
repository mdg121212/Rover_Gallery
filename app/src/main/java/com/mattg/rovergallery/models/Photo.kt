package com.mattg.rovergallery.models

import android.widget.ImageView
import androidx.databinding.BindingAdapter

/**
 * Represents a photo object displayed in both main screen list and detail screen
 */
data class Photo (
    var id: Int = 0,
    var sol: Int = 0,
    var camera: Camera? = null,
    var img_src: String? = null,
    var earth_date: String? = null,
    var rover: Rover? = null,
) {
    fun solTitle(): String = "Sol: $sol"
    fun earthTitle(): String = "Earth Date: $earth_date"

    companion object {
        @JvmStatic
        @BindingAdapter("marsImage")
        fun loadImage(view: ImageView, profileImage: String) {
            view.apply {
                loadImage(view, profileImage)
            }
        }
    }
}
