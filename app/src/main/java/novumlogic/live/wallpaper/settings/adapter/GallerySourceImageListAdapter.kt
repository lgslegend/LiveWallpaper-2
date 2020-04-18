package novumlogic.live.wallpaper.settings.adapter

import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.item_gallery_source_image_list.view.*
import novumlogic.live.wallpaper.R
import java.io.File
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import novumlogic.live.wallpaper.settings.listeners.OnShowHideOptions


/**
 * Created by Priya Sindkar.
 */
class GallerySourceImageListAdapter(var imageItems: MutableList<String>, private val onShowHideOptions: OnShowHideOptions) : RecyclerView.Adapter<GallerySourceImageListAdapter.ImageItemViewHolder>() {
    var listOfImagesToRemove = ArrayList<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ImageItemViewHolder {
        return ImageItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_gallery_source_image_list, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val imageItem = imageItems[position]

        if (listOfImagesToRemove.contains(imageItem)) {
            holder.itemView.imgRemove.visibility = View.VISIBLE
        } else {
            holder.itemView.imgRemove.visibility = View.GONE
        }

        val imageRequest = if (imageItem.isNullOrEmpty()) {
            ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.default_image)
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
        } else {
            if (imageItem.startsWith("file:/")) {
                ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(imageItem))
                        .setResizeOptions(ResizeOptions(200, 200))
                        .build()
            } else {
                ImageRequestBuilder
                        .newBuilderWithSource(Uri.fromFile(File(imageItem)))
                        .setResizeOptions(ResizeOptions(200, 200))
                        .build()
            }
        }

        holder.itemView.imgWallpaper.setImageRequest(imageRequest)

        holder.itemView.setOnClickListener {
            if (listOfImagesToRemove.contains(imageItem)) {
                scaleView(holder.itemView.imgWallpaper, 0.8f, 1f)
                holder.itemView.imgRemove.visibility = View.GONE
                listOfImagesToRemove.remove(imageItem)
            } else {
                scaleView(holder.itemView.imgWallpaper, 1f, 0.8f)
                holder.itemView.imgRemove.visibility = View.VISIBLE
                listOfImagesToRemove.add(imageItem)
            }
            if (listOfImagesToRemove.isEmpty()) {
                onShowHideOptions.showHideDelete(false)
            } else {
                onShowHideOptions.showHideDelete(true)
            }
        }
    }

    fun getImageList(): ArrayList<String> {
        return imageItems.filter { !listOfImagesToRemove.contains(it) } as ArrayList<String>
    }

    private fun scaleView(v: View, startScale: Float, endScale: Float) {
        val anim = ScaleAnimation(startScale, endScale, startScale, endScale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.fillAfter = true
        anim.duration = 100
        v.startAnimation(anim)
    }

    class ImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}