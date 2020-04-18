package novumlogic.live.wallpaper.settings.adapter

import android.graphics.Color
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.common.internal.Preconditions
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.item_image_list.view.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.apiHelper.ApiConfig
import novumlogic.live.wallpaper.apiHelper.Wallpaper
import novumlogic.live.wallpaper.settings.listeners.OnImageSelectedListener
import java.io.File


/**
 * Created by Priya Sindkar.
 */
class ImageListAdapter(private val imageItems: List<Wallpaper>, private val onImageSelectedListener: OnImageSelectedListener) : RecyclerView.Adapter<ImageListAdapter.ImageItemViewHolder>() {
    lateinit var imageItem: Wallpaper

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ImageItemViewHolder {
        return ImageItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_image_list, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        imageItem = imageItems[position]

        val imageRequest = when {
            imageItem.imagePath.isNullOrEmpty() -> ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.splash)
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
            imageItem.imagePath.startsWith("/data") -> // saved in phone memory
                ImageRequestBuilder
                        .newBuilderWithSource(Uri.fromFile(File(imageItem.imagePath)))
                        .setResizeOptions(ResizeOptions(200, 200))
                        .build()
            else -> ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(ApiConfig.BASE_URL.plus(imageItem.imagePath)))
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()
        }

        holder.itemView.imgWallpaper.setImageRequest(imageRequest)

        if (imageItem.imagePath.startsWith("/data")) {
            setShowBorder(holder.itemView.imgWallpaper, true)
        } else {
            setShowBorder(holder.itemView.imgWallpaper, false)
        }

        holder.itemView.setOnClickListener {
            imageItem = imageItems[position]
            onImageSelectedListener.onImageSelected(ApiConfig.BASE_URL.plus(imageItem.imagePath), imageItem.id)
        }
    }

    private fun setShowBorder(draweeView: SimpleDraweeView, show: Boolean/*, scaleInside: Boolean*/) {
        val roundingParams = Preconditions.checkNotNull(draweeView.hierarchy.roundingParams)
        if (show) {
            roundingParams!!.setBorder(
                    ContextCompat.getColor(draweeView.context, R.color.colorAccent),
                    draweeView.context.resources.getDimensionPixelSize(R.dimen.margin_1).toFloat())
//            roundingParams.scaleDownInsideBorders = scaleInside
        } else {
            roundingParams!!.setBorder(Color.TRANSPARENT, 0f)
        }
        draweeView.hierarchy.roundingParams = roundingParams
    }

    class ImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}