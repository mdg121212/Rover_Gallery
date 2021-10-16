package com.mattg.rovergallery.utils

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mattg.rovergallery.R



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
