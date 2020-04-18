package novumlogic.live.wallpaper.settings.adapter

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
import novumlogic.live.wallpaper.apiHelper.MoreApp
import novumlogic.live.wallpaper.settings.listeners.OnAppItemSelectedListener


/**
 * Created by Priya Sindkar.
 */
class AppsListAdapter(private val imageItems: List<MoreApp>, private val onAppItemSelectedListener: OnAppItemSelectedListener) : RecyclerView.Adapter<AppsListAdapter.AppsItemViewHolder>() {

    lateinit var imageItem: MoreApp
    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AppsItemViewHolder {
        return AppsItemViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_ad_list, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: AppsItemViewHolder, position: Int) {
        imageItem = imageItems[position]
        if (imageItem.imagePath.isNullOrEmpty()) {
            val imageRequest = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.splash)
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()

            holder.itemView.imgWallpaper.setImageRequest(imageRequest)
        } else {
            val imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(ApiConfig.BASE_URL.plus(imageItem.imagePath)))
                    .setResizeOptions(ResizeOptions(200, 200))
                    .build()

            holder.itemView.imgWallpaper.setImageRequest(imageRequest)
        }

        holder.itemView.setOnClickListener {
            imageItem = imageItems[position]
            onAppItemSelectedListener.onAppItemSelected(imageItem.url)
        }
    }

    class AppsItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}