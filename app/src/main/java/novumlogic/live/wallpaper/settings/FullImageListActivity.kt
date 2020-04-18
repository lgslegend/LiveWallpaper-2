package novumlogic.live.wallpaper.settings

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_full_image_list.*
import kotlinx.android.synthetic.main.activity_full_image_list.backDrop
import kotlinx.android.synthetic.main.activity_full_image_list.progressBar
import kotlinx.android.synthetic.main.activity_full_image_list.recyclerBackgroundImages
import kotlinx.android.synthetic.main.activity_full_image_list.toolbar
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.progress_dialog.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.apiHelper.APIResponseModel
import novumlogic.live.wallpaper.apiHelper.Wallpaper
import novumlogic.live.wallpaper.settings.adapter.FullImageListAdapter
import novumlogic.live.wallpaper.settings.adapter.ImageListAdapter
import novumlogic.live.wallpaper.settings.listeners.OnImageSelectedListener
import novumlogic.live.wallpaper.utility.*

/**
 * Created by Priya Sindkar.
 */
class
FullImageListActivity : AppCompatActivity(), OnImageSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBar()
        setContentView(R.layout.activity_full_image_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val jsonList = getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).getString(AppConstants.APP_IMAGE_INFO, "")
        val cacheData = Gson().fromJson(jsonList, APIResponseModel::class.java)
        // processResponse(cacheData)
        loaddatata()

    }

    // toolbar back press event
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun loaddatata() {
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


        // this.listOfImages = arrayList as ArrayList<Wallpaper>
        //val previewImageSelected = fetchImageToPreview()

        progressBar.visibility = View.GONE
//        if (response.appDatumModels != null && response.appDatumModels.isNotEmpty()) {
//            val listOfImages = response.appDatumModels[0].wallpapers

        val imageListAdapter = FullImageListAdapter(arrayList, this)
        recyclerBackgroundImages.layoutManager = GridLayoutManager(this, 3)
        recyclerBackgroundImages.setHasFixedSize(true)
        recyclerBackgroundImages.setItemViewCacheSize(0)
        recyclerBackgroundImages.adapter = imageListAdapter
    }


    override fun onAdSelected(redirectURL: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onBackPressed() {
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

    private fun processResponse(response: APIResponseModel) {
        progressBar.visibility = View.GONE
        if (response.appDatumModels != null && response.appDatumModels.isNotEmpty()) {
            val listOfImages = response.appDatumModels[0].wallpapers

            val imageListAdapter = FullImageListAdapter(listOfImages, this)
            recyclerBackgroundImages.layoutManager = GridLayoutManager(this, 3)
            recyclerBackgroundImages.setHasFixedSize(true)
            recyclerBackgroundImages.setItemViewCacheSize(0)
            recyclerBackgroundImages.adapter = imageListAdapter
        }
    }

    /**
     *
     * Image selected from list of images for setting as wallpaper
     */
    override fun onImageSelected(imageURI: String, imageId: String) {
        val imagePath = ImageUtils.loadFilePathFromStorage(this, imageId)
        if (imagePath == "" && !checkInternetConnection()) {
            showNoInternetDialog()
        } else {
            cardProgress.visibility = View.VISIBLE
            backDrop.visibility = View.VISIBLE
            disableUserTouch()
            AsyncTask.execute {
                ImageUtils.saveToInternalStorage(this, imageURI, imageId)
                saveWallpaperType(AppConstants.WallpaperType.ONLINE_IMAGE.typeId)
                runOnUiThread {
                    cardProgress.visibility = View.GONE
                    backDrop.visibility = View.GONE
                    enableUserTouch()
                }
                finish()
            }
        }
    }

}