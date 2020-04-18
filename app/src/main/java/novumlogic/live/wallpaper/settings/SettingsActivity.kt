package novumlogic.live.wallpaper.settings

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.apiHelper.Wallpaper
import novumlogic.live.wallpaper.settings.adapter.ImageListAdapter
import novumlogic.live.wallpaper.settings.listeners.OnAppItemSelectedListener
import novumlogic.live.wallpaper.settings.listeners.OnAutoWallpaperSourceSelected
import novumlogic.live.wallpaper.settings.listeners.OnImageSelectedListener
import novumlogic.live.wallpaper.utility.*
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SettingsActivity : AppCompatActivity(), OnImageSelectedListener, OnAppItemSelectedListener, OnAutoWallpaperSourceSelected {

    private val SELECT_FILE = 1
    private var fileUri: Uri? = null
    private var selectedImagePath: String = ""
    private var listOfImages = ArrayList<Wallpaper>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLightStatusBar()

        setContentView(R.layout.activity_settings)


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        rippleSwitch.isChecked = fetchRippleSetting()
        musicSwitch.isChecked = fetchBackgroundMusicSetting()
        rainDropSwitch.isChecked = fetchRainDropSetting()

        musicSwitch.setOnCheckedChangeListener { _, p1 ->

            saveBackgroundMusicSetting(p1)
        }

        rippleSwitch.setOnCheckedChangeListener { _, p1 ->

            saveRippleSetting(p1)
        }

        rainDropSwitch.setOnCheckedChangeListener { _, p1 ->

            saveRainDropSetting(p1)
        }

        GlobalScope.launch {
           loaddatata()

        }

        when (fetchWallpaperType()) {
            AppConstants.WallpaperType.GALLERY.typeId -> {
                selectedImagePath = fetchImageToPreview()
                showGalleryImagePreview()
                showGalleryHighlight()
            }
            AppConstants.WallpaperType.ONLINE_IMAGE.typeId -> {
                imgGalleryPreview.visibility = View.GONE
                showChooseImageHighlight()

            }
            AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId -> {
                imgGalleryPreview.visibility = View.GONE
                showWallpaperChangerHighlight()

            }
            AppConstants.WallpaperType.AUTO_ROTATE_ONLINE_IMAGE.typeId -> {
                imgGalleryPreview.visibility = View.GONE
                showWallpaperChangerHighlight()
            }
        }

        constraintFromGallery.setOnClickListener {
            selectImage()
        }

        constraintChooseImages.setOnClickListener {
            showChooseImageHighlight()
            startActivity(Intent(this, FullImageListActivity::class.java))
            finish()
        }

        arrow.setOnClickListener {
            showChooseImageHighlight()
            startActivity(Intent(this, FullImageListActivity::class.java))
            finish()
        }

        constraintWallpaperCycle.setOnClickListener {
            val bottomDialog = AutoWallpaperSourceBottomDialogFragment()
            bottomDialog.onAutoWallpaperSourceSelected = this
            bottomDialog.show(supportFragmentManager, AutoWallpaperSourceBottomDialogFragment::class.java.name)
        }
    }

    // toolbar back press event
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    /**
     * Result callback from permission
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsUtil.FILES_READ_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent()
                } else {
                    //code for deny
                }
            }
        }
    }

    /**
     *
     * Result Callback for image picker (camera or gallery)
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                val path = FilePathUtils.getPath(this, data!!.data)
                showImageFromGallery(path)
            }
        }
    }

    /**
     *
     * Ad item selected-> Go to play store
     */
    override fun onAppItemSelected(redirectURL: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(redirectURL)))
    }

    /**
     *
     * Ad item selected-> Go to play store
     */
    override fun onAdSelected(redirectURL: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(redirectURL)))
    }

    /**
     *
     * Image selected from list of images for setting as wallpaper
     */
    override fun onImageSelected(imageURI: String, imageId: String) {
        val imagePath = ImageUtils.loadFilePathFromStorage(this, imageId)
        if (imagePath == "" ) {
            Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
        } else {
            saveWallpaperType(AppConstants.WallpaperType.ONLINE_IMAGE.typeId)
            constraintFromGallery.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
            constraintChooseImages.background = ContextCompat.getDrawable(this, R.drawable.option_selected)

            cardProgress.visibility = View.VISIBLE
            backDrop.visibility = View.VISIBLE
            disableUserTouch()

            AsyncTask.execute {
                ImageUtils.saveToInternalStorage(this, imageURI, imageId)
                runOnUiThread {
                    cardProgress.visibility = View.GONE
                    backDrop.visibility = View.GONE
                    enableUserTouch()
                }
                finish()
            }
        }
    }

    /**
     *
     * Callback from wallpaper source selection
     */
    override fun onSourceSelected(sourceId: Int) {
        when (sourceId) {
            AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId -> {
                startActivity(Intent(this, GallerySourceImageListActivity::class.java))
                finish()
            }
            AppConstants.WallpaperType.AUTO_ROTATE_ONLINE_IMAGE.typeId -> {
                startActivity(Intent(this, OnlineSourceImageListActivity::class.java))
                finish()
            }
        }
    }

    /**
     * Check permission and open gallery for image selection
     */
    private fun selectImage() {
        if (PermissionsUtil.checkPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            galleryIntent()
        }
    }

    /**
     *
     * Open gallery for image selection for room
     */
    private fun galleryIntent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(Intent.createChooser(intent, "Upload Image"), SELECT_FILE)
    }

    private fun showImageFromGallery(data: String) {
        if (data != null) {
            try {
                selectedImagePath = data
                saveImageToPreview(selectedImagePath)
                saveWallpaperType(AppConstants.WallpaperType.GALLERY.typeId)
                finish()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     *
     * show selected gallery image preview
     */
    private fun showGalleryImagePreview() {
        val imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.fromFile(File(selectedImagePath)))
                .setResizeOptions(ResizeOptions(200, 200))
                .build()
        imgGalleryPreview?.setImageRequest(imageRequest)
        imgGalleryPreview.visibility = View.VISIBLE
    }

    /**
     *
     * Call API
     */


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




        this.listOfImages = arrayList as ArrayList<Wallpaper>
        val previewImageSelected = fetchImageToPreview()
        val imageListAdapter = if (previewImageSelected.isNullOrEmpty() || fetchWallpaperType() != AppConstants.WallpaperType.ONLINE_IMAGE.typeId) {
            ImageListAdapter(listOfImages.subList(0, 3), this)
        } else {
            val imageId = previewImageSelected.substringAfterLast("/")
            val listToDisplay = listOfImages.filter { it.id != imageId } as MutableList<Wallpaper>
            listToDisplay.add(0, Wallpaper(imageId, "", previewImageSelected))
            ImageListAdapter(listToDisplay.subList(0, 3), this)
        }

        recyclerBackgroundImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerBackgroundImages.setHasFixedSize(true)
        recyclerBackgroundImages.setItemViewCacheSize(0)
        recyclerBackgroundImages.adapter = imageListAdapter

    }
//    private fun fetchDataFromAPI(fcmToken: String) {
//        val fields = Build.VERSION_CODES::class.java.fields
//        val osName = fields[Build.VERSION.SDK_INT].name
//
//        ApiConfig.getRetrofit(ApiConfig.BASE_URL).create(ApiService::class.java)
//                .fetchApiData(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID), fcmToken, osName).enqueue(
//                        object : Callback<APIResponseModel> {
//                            override fun onFailure(call: Call<APIResponseModel>, t: Throwable) {
//                                processResponse(fetchAPIInfo())
//                            }
//
//                            override fun onResponse(call: Call<APIResponseModel>, response: Response<APIResponseModel>) {
//                                if (response != null && response.isSuccessful && response.body() != null) {
//                                    saveAPIInfo(response.body()!!)//getSharedPreferences(AppConstants.APP_PREFS_CONTEXT, 0).edit().putString(AppConstants.APP_IMAGE_INFO, Gson().toJson(response.body())).apply()
//                                    processResponse(response.body()!!)
//                                }
//                            }
//                        })
//    }

    /**
     *
     * Process API response and show data to user
     */
//    private fun processResponse(response: APIResponseModel?) {
//        if (response?.appDatumModels != null && response.appDatumModels.isNotEmpty()) {
//
//            val cars = listOf(Wallpaper("0", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"), Wallpaper("1", "6300","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg") )
//            val listOfImages = cars as MutableList
//            this.listOfImages = listOfImages as ArrayList<Wallpaper>
//             //listOfImages.toMutableList().add(Wallpaper(0,"aaa","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg"))
//
//            val listOfMoreApps = response.appDatumModels[0].moreApps
//
//            val previewImageSelected = fetchImageToPreview()
//            val imageListAdapter = if (previewImageSelected.isNullOrEmpty() || fetchWallpaperType() != AppConstants.WallpaperType.ONLINE_IMAGE.typeId) {
//                ImageListAdapter(listOfImages.subList(0, 3), this)
//            } else {
//                val imageId = previewImageSelected.substringAfterLast("/")
//                val listToDisplay = listOfImages.filter { it.id != imageId } as MutableList<Wallpaper>
//                listToDisplay.add(0, Wallpaper(imageId, "", previewImageSelected))
//                ImageListAdapter(listToDisplay.subList(0, 3), this)
//            }
//
//            recyclerBackgroundImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            recyclerBackgroundImages.setHasFixedSize(true)
//            recyclerBackgroundImages.setItemViewCacheSize(0)
//            recyclerBackgroundImages.adapter = imageListAdapter
//
//            val appsListAdapter = AppsListAdapter(listOfMoreApps, this)
//            recyclerApps.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//            recyclerApps.setHasFixedSize(true)
//            recyclerApps.setItemViewCacheSize(0)
//            recyclerApps.adapter = appsListAdapter
//        }
//
//    }

//    private fun   {
//        progressBarImageList.visibility = View.GONE
//        progressBarApps.visibility = View.GONE
//    }

    /**
     *
     * Fetch FCM token for passing in API request field
     */
    private suspend fun fetchFCMToken(): String = suspendCoroutine { continuation ->
        // Get token
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener<InstanceIdResult> { task ->
                    if (!task.isSuccessful) {
                        continuation.resume("")
                        return@OnCompleteListener
                    }

                    if (task.result != null) {
                        continuation.resume(task.result!!.token)
                    } else {
                        continuation.resume("")
                    }
                })
    }

    private fun showChooseImageHighlight() {
        constraintChooseImages.background = ContextCompat.getDrawable(this, R.drawable.option_selected)
        constraintFromGallery.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
        constraintWallpaperCycle.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
    }

    private fun showGalleryHighlight() {
        constraintFromGallery.background = ContextCompat.getDrawable(this, R.drawable.option_selected)
        constraintChooseImages.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
        constraintWallpaperCycle.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
    }

    private fun showWallpaperChangerHighlight() {
        constraintWallpaperCycle.background = ContextCompat.getDrawable(this, R.drawable.option_selected)
        constraintChooseImages.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
        constraintFromGallery.background = ContextCompat.getDrawable(this, R.drawable.option_unselected)
    }
}