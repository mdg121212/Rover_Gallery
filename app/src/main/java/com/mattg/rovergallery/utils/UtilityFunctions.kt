package com.mattg.rovergallery.utils

import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mattg.rovergallery.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.R.string
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


fun getProgressDrawable(context: Context) : CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 10f //creates a spinner to display in image before it is loaded
            centerRadius = 50f
            start()
        }
    }

    fun ImageView.loadImage(imageString: String?, progressDrawable: CircularProgressDrawable) {
        val options = RequestOptions()
            .placeholder(progressDrawable)
            .error(R.drawable.icon_spinner)
        Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(imageString)
            .into(this)

    }

fun String.toLocalDate(): LocalDate? {
    return if(this.isNotEmpty() && this.length == 10) {
        val returnFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(this, returnFormat)
    } else {
        null
    }
}

fun Long.fromCalenderSelection(): String {
    val returnFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val ldt: LocalDateTime =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime() ;
    //material calendar selection was returning 1 day behind, this accounts for that
    return returnFormat.format(ldt.plusDays(1L)).toString()


}

