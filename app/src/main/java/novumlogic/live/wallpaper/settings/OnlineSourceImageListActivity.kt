package novumlogic.live.wallpaper.settings

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_image_list.*
import kotlinx.android.synthetic.main.activity_image_list.backDrop
import kotlinx.android.synthetic.main.activity_image_list.recyclerBackgroundImages
import kotlinx.android.synthetic.main.activity_image_list.toolbar
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.progress_dialog.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.apiHelper.APIResponseModel
import novumlogic.live.wallpaper.apiHelper.ApiConfig
import novumlogic.live.wallpaper.apiHelper.Wallpaper
import novumlogic.live.wallpaper.settings.adapter.ImageListAdapter
import novumlogic.live.wallpaper.settings.adapter.OnlineSourceImageListAdapter
import novumlogic.live.wallpaper.utility.*

/**
 * Created by Priya Sindkar.
 */
class OnlineSourceImageListActivity : AppCompatActivity() {
    private lateinit var imageListAdapter: OnlineSourceImageListAdapter
    var hasPendingDownload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBar()
        setContentView(R.layout.activity_image_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Choose Images"

        btnSet.setOnClickListener {
            val map = imageListAdapter.listOfImagesSelected
            when {
                map.size <= 1 -> Toast.makeText(applicationContext, getString(R.string.msg_min_images_auto_change), Toast.LENGTH_LONG).show()
                map.size > 10 -> Toast.makeText(applicationContext, getString(R.string.msg_max_images_auto_change), Toast.LENGTH_LONG).show()
                else -> setAutoWallpaperChangeFromOnline(map)
            }
        }
loaddatata()
//        processResponse(fetchAPIInfo())
    }

    // toolbar back press event
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }


    private fun setAutoWallpaperChangeFromOnline(map: HashMap<String, String>) {
        downloadProgressBar.isIndeterminate = false
        cardProgress.visibility = View.VISIBLE
        backDrop.visibility = View.VISIBLE
        disableUserTouch()
        AsyncTask.execute {
            val isSuccess = downloadAllWallpapersToLocal(map)
            runOnUiThread {
                enableUserTouch()
                cardProgress.visibility = View.GONE
                backDrop.visibility = View.GONE
                if (hasPendingDownload) {
//                    showNoInternetDialog(isSuccess)
                } else {
                    proceedWithAutoWallpaperSet(isSuccess)
                }
            }
        }
    }

    /**
     *
     * Download online images to local storage for auto rotate wallpaper
     */
    private fun downloadAllWallpapersToLocal(map: HashMap<String, String>): Boolean {
        val listOfImagePaths = HashMap<String, String>()
        val count = map.size
        downloadProgressBar.max = count

        map.asIterable().forEachIndexed { index, entry ->
            val imagePath = ImageUtils.loadFilePathFromStorage(this, entry.key)
            runOnUiThread {
                txtProgress.text = "Downloading Images (${index + 1}/$count) ..."
                downloadProgressBar.progress = index + 1
            }
            if (imagePath == "") {

                    listOfImagePaths[entry.key] = ImageUtils.saveToInternalStorage(this, ApiConfig.BASE_URL.plus(entry.value), entry.key)

            } else {
                listOfImagePaths[entry.key] = imagePath
            }
        }

        return if (listOfImagePaths.size >= 2) {
            saveAutoWallpaperOnlineList(listOfImagePaths)
            saveWallpaperType(AppConstants.WallpaperType.AUTO_ROTATE_ONLINE_IMAGE.typeId)
            true
        } else {
            false
        }
    }


    private fun loaddatata(){
//        val cars = listOf(Wallpaper("0", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("1", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("2", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("3", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("4", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("5", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg") )
        var img=R.drawable.default_image
//        val cars = listOf(Wallpaper("0", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("1", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("2", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("3", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("4", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("5", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg") )
        val arrayList = ArrayList<Wallpaper>()//Creating an empty arraylist
        arrayList.add(Wallpaper("0", "0", (R.drawable.img1).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("1", "1", (R.drawable.img2).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("2", "2", (R.drawable.img3).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("3", "3", (R.drawable.img4).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("4", "4", (R.drawable.img5).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("5", "5", (R.drawable.img6).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("6", "6", (R.drawable.img7).toString()))//Adding object in arraylist
        arrayList.add(Wallpaper("7", "7", (R.drawable.img8).toString()))//Adding object in arraylist





        imageListAdapter = OnlineSourceImageListAdapter(arrayList)

        recyclerBackgroundImages.layoutManager = GridLayoutManager(this, 3)
        recyclerBackgroundImages.setHasFixedSize(true)
        recyclerBackgroundImages.setItemViewCacheSize(0)
        recyclerBackgroundImages.adapter = imageListAdapter

    }
//    private fun processResponse(response: APIResponseModel?) {
//        progressBar.visibility = View.GONE
//        if (response?.appDatumModels != null && response.appDatumModels.isNotEmpty()) {
//            emptyText.visibility = View.GONE
//            val listOfImages = response.appDatumModels[0].wallpapers as MutableList
//            imageListAdapter = OnlineSourceImageListAdapter(listOfImages)
//
//            recyclerBackgroundImages.layoutManager = GridLayoutManager(this, 3)
//            recyclerBackgroundImages.setHasFixedSize(true)
//            recyclerBackgroundImages.setItemViewCacheSize(0)
//            recyclerBackgroundImages.adapter = imageListAdapter
//        } else {
//            emptyText.visibility = View.VISIBLE
//            emptyText.text = "No Images Found"
//        }
//    }

    private fun showNoInternetDialog(isSuccess: Boolean) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val message = if (isSuccess) {
            getString(R.string.msg_min_images_auto_change_internet)
        } else {
            getString(R.string.msg_min_images_auto_change_internet).plus(getString(R.string.msg_require_2_downloads))
        }
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setCancelable(false)

        alertDialogBuilder.setPositiveButton(getString(R.string.label_okay)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.setOnDismissListener {
            proceedWithAutoWallpaperSet(isSuccess)
        }
        alertDialog.show()
    }

    private fun proceedWithAutoWallpaperSet(isSuccess: Boolean) {
        if (isSuccess) {
            Toast.makeText(this, getString(R.string.msg_2_min_wallpaper_change_set), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}