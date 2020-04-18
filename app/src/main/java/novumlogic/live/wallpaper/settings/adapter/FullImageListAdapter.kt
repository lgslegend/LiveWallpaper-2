package novumlogic.live.wallpaper.settings.adapter

import android.content.ContentResolver
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.item_image_list.view.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.apiHelper.ApiConfig
import novumlogic.live.wallpaper.apiHelper.Wallpaper
import novumlogic.live.wallpaper.settings.listeners.OnImageSelectedListener


/**
 * Created by Priya Sindkar.
 */
class FullImageListAdapter(private val imageItems: List<Wallpaper>, private val onImageSelectedListener: OnImageSelectedListener) : RecyclerView.Adapter<FullImageListAdapter.ImageItemViewHolder>() {
    lateinit var imageItem: Wallpaper

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ImageItemViewHolder {
        return ImageItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_full_image_list, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        imageItem = imageItems[position]

        val imageRequest = if (imageItem.imagePath.isNullOrEmpty()) {
            ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.splash)
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
        } else {


            ImageRequestBuilder

                    .newBuilderWithSource(Uri.parse(ApiConfig.BASE_URL.plus(imageItem.imagePath)))
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
        }

        holder.itemView.imgWallpaper.setImageRequest(imageRequest)

        holder.itemView.setOnClickListener {
            imageItem = imageItems[position]
            onImageSelectedListener.onImageSelected(ApiConfig.BASE_URL.plus(imageItem.imagePath), imageItem.id)
        }
    }

    class ImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}