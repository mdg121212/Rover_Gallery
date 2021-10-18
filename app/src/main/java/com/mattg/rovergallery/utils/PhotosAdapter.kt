package com.mattg.rovergallery

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mattg.rovergallery.databinding.ItemRvPhotoBinding
import com.mattg.rovergallery.models.Photo
import com.mattg.rovergallery.utils.getProgressDrawable
import com.mattg.rovergallery.utils.loadImage

/**
 * Adapter class to handle paginated Photo data from the Nasa API
 */
class PhotosAdapter : PagingDataAdapter<Photo, PhotoViewHolder>(PhotoComparator) {

    private lateinit var callback: RecyclerCallback
    private lateinit var context: Context

   fun checkValue() : Boolean {
       return this.itemCount == 0
   }

    fun setCallBack(callbackIn: RecyclerCallback) {
        this.callback = callbackIn
    }
    fun setContext(contextIn: Context) {
        this.context = contextIn
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, context, callback)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder.from(parent)
    }

}

/**
 * Object extending DiffUtil callback and compare given photo objects
 */
 object PhotoComparator : DiffUtil.ItemCallback<Photo>() {

        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem.sol == newItem.sol

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
            oldItem == newItem
    }

/**
 * View Holder class for the gallery
 */
class PhotoViewHolder private constructor(private val binding: ItemRvPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): PhotoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRvPhotoBinding.inflate(layoutInflater, parent, false)
            return PhotoViewHolder(binding)
        }
    }

    /**
     * Binds the photo to the view holder
     * TODO implement actual data binding
     */
    fun bind(photoIn: Photo, context: Context, clickListener: RecyclerCallback) {
        Log.d("DATATEST", "BINDING AN ITEM")

        binding.ivPhoto.apply {
            loadImage(photoIn.img_src, getProgressDrawable(context))
            setOnClickListener {
                clickListener.photoClicked(
                    photo = photoIn,
                    position = absoluteAdapterPosition
                )
            }
        }
        binding.root.apply {
            animation = AlphaAnimation(0.0f, 1.0f)
            animation.duration = 1200
        }
    }
}





