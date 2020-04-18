package novumlogic.live.wallpaper.settings.adapter

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.item_gallery_source_image_list.view.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.apiHelper.ApiConfig
import novumlogic.live.wallpaper.apiHelper.Wallpaper
import novumlogic.live.wallpaper.utility.fetchAutoWallpaperOnlineList


/**
 * Created by Priya Sindkar.
 */
class OnlineSourceImageListAdapter(private val imageItems: List<Wallpaper>) : RecyclerView.Adapter<OnlineSourceImageListAdapter.ImageItemViewHolder>() {
    lateinit var imageItem: Wallpaper
    lateinit var listOfImagesSelected: HashMap<String, String>

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ImageItemViewHolder {
        listOfImagesSelected = viewGroup.context.fetchAutoWallpaperOnlineList()
        return ImageItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_online_source_image_list, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        imageItem = imageItems[position]

        val imageRequest = if (imageItem.imagePath.isNullOrEmpty()) {
            ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.default_image)
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
        } else {
            ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(ApiConfig.BASE_URL.plus(imageItem.imagePath)))
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
        }

        if (listOfImagesSelected.containsKey(imageItem.id)) {
            holder.itemView.imgWallpaper.scaleX = 0.8f
            holder.itemView.imgWallpaper.scaleY = 0.8f
            holder.itemView.imgRemove.visibility = View.VISIBLE
        } else {
            holder.itemView.imgWallpaper.scaleX = 1f
            holder.itemView.imgWallpaper.scaleY = 1f
            holder.itemView.imgRemove.visibility = View.GONE
        }

        holder.itemView.imgWallpaper.setImageRequest(imageRequest)

        holder.itemView.setOnClickListener {
            imageItem = imageItems[position]
            if (listOfImagesSelected.containsKey(imageItem.id)) {
                holder.itemView.imgWallpaper.scaleX = 1f
                holder.itemView.imgWallpaper.scaleY = 1f
//                scaleView(holder.itemView.imgWallpaper, 0.8f, 1f)
                holder.itemView.imgRemove.visibility = View.GONE
                listOfImagesSelected.remove(imageItem.id)
            } else {
                if (listOfImagesSelected.size == 10) {
                    Toast.makeText(holder.itemView.context, holder.itemView.context.getString(R.string.msg_max_images_auto_change), Toast.LENGTH_LONG).show()
                } else {
                    holder.itemView.imgWallpaper.scaleX = 0.8f
                    holder.itemView.imgWallpaper.scaleY = 0.8f
//                    scaleView(holder.itemView.imgWallpaper, 1f, 0.8f)
                    holder.itemView.imgRemove.visibility = View.VISIBLE
                    listOfImagesSelected[imageItem.id] = imageItem.imagePath
                }
            }
        }
    }

    private fun scaleView(v: View, startScale: Float, endScale: Float) {
        val anim = ScaleAnimation(startScale, endScale, startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.fillAfter = true
        anim.duration = 50
        v.startAnimation(anim)
    }

    class ImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}