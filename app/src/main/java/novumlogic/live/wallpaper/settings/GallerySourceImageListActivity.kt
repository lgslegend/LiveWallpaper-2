package novumlogic.live.wallpaper.settings

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_image_list.*
import novumlogic.live.wallpaper.R
import novumlogic.live.wallpaper.settings.adapter.GallerySourceImageListAdapter
import novumlogic.live.wallpaper.settings.listeners.OnShowHideOptions
import novumlogic.live.wallpaper.utility.*

/**
 * Created by Priya Sindkar.
 */
class GallerySourceImageListActivity : AppCompatActivity(), OnShowHideOptions {
    private var showDelete = false
    private var imageListAdapter: GallerySourceImageListAdapter? = null
    private val SELECT_MULTIPLE_FILES = 10
    private var fileUri: Uri? = null
    private var isChoosenFromGallery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightStatusBar()
        setContentView(R.layout.activity_image_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gallery Images"

        recyclerBackgroundImages.layoutManager = GridLayoutManager(this, 3)
        recyclerBackgroundImages.setHasFixedSize(true)
        recyclerBackgroundImages.setItemViewCacheSize(0)

        btnSet.setOnClickListener {
            if (imageListAdapter != null) {
                if (imageListAdapter!!.imageItems.size <= 1) {
                    Toast.makeText(applicationContext, getString(R.string.msg_min_images_auto_change), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                saveAutoWallpaperGalleryList(imageListAdapter!!.imageItems as ArrayList<String>)
                saveWallpaperType(AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId)
                Toast.makeText(applicationContext, getString(R.string.msg_2_min_wallpaper_change_set), Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(applicationContext, getString(R.string.msg_min_images_auto_change), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (PermissionsUtil.checkPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            if (isChoosenFromGallery) {
                isChoosenFromGallery = false
            } else {
                processResponse(fetchAutoWallpaperGalleryList())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.gallery_images_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu?.getItem(0)?.isVisible = showDelete
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if (item?.itemId == R.id.action_add) {
            if (imageListAdapter != null && imageListAdapter!!.imageItems.size == 10) {
                Toast.makeText(this, getString(R.string.msg_err_max_image_selection), Toast.LENGTH_SHORT).show()
            } else {
                selectImage()
            }
            return true
        }

        if (item?.itemId == R.id.action_delete) {
            val refreshedImageList = imageListAdapter!!.getImageList()
            saveAutoWallpaperGalleryList(refreshedImageList)
            imageListAdapter!!.listOfImagesToRemove = ArrayList<String>()
            imageListAdapter!!.imageItems = refreshedImageList
            imageListAdapter!!.notifyDataSetChanged()

            if (refreshedImageList.isNullOrEmpty()) {
                showEmptyText()
            } else {
                emptyText.visibility = View.GONE
            }
            showHideDelete(false)
            return true
        }
        return false
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

    /**
     * Result callback from permission
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsUtil.FILES_READ_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntentForMultiple()
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
            if (requestCode == SELECT_MULTIPLE_FILES) {
                saveImagePathForAutoRotate(data)
            }
        }
    }

    override fun showHideDelete(isShow: Boolean) {
        showDelete = isShow
        invalidateOptionsMenu()
    }

    /**
     * Check permission and open gallery for image selection
     */
    private fun selectImage() {
        if (PermissionsUtil.checkPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            galleryIntentForMultiple()
        }
    }

    /**
     *
     * Open gallery for multiple image selection
     */
    private fun galleryIntentForMultiple() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.putExtra((Intent.EXTRA_ALLOW_MULTIPLE), true)
        startActivityForResult(Intent.createChooser(intent, "Select Images"), SELECT_MULTIPLE_FILES)
    }

    /**
     *
     * Save paths of all images selected from gallery for auto change wallpaper image
     */
    private fun saveImagePathForAutoRotate(data: Intent?) {
        if (data!!.clipData != null) {
            val count = data.clipData.itemCount

            if (imageListAdapter != null && imageListAdapter!!.imageItems.size + count > 10) {
                Toast.makeText(this, getString(R.string.msg_max_images_auto_change), Toast.LENGTH_SHORT).show()
            } else {
                var i = 0
                val listOfImages = ArrayList<String>()
                while (i < count) {
                    val path = FilePathUtils.getPath(this, data.clipData.getItemAt(i++).uri)
                    listOfImages.add(path)
                }
                if (imageListAdapter == null) {
                    imageListAdapter = GallerySourceImageListAdapter(listOfImages, this)
                    recyclerBackgroundImages.adapter = imageListAdapter
                } else {
                    imageListAdapter!!.imageItems.addAll(listOfImages)
                    imageListAdapter!!.notifyDataSetChanged()
                }
                imageListAdapter!!.listOfImagesToRemove = ArrayList<String>()
                if (listOfImages.isNotEmpty()) {
                    emptyText.visibility = View.GONE
                } else {
                    showEmptyText()
                }
                isChoosenFromGallery = true
            }
        }
    }

    private fun processResponse(listOfImages: java.util.ArrayList<*>) {
        progressBar.visibility = View.GONE
        if (listOfImages != null && listOfImages.isNotEmpty()) {
            emptyText.visibility = View.GONE
            imageListAdapter = GallerySourceImageListAdapter(listOfImages as ArrayList<String>, this)
            recyclerBackgroundImages.adapter = imageListAdapter
        } else {
            showEmptyText()
        }
    }

    private fun showEmptyText() {
        emptyText.visibility = View.VISIBLE
        emptyText.text = getString(R.string.msg_empty_image_list)
        emptyText.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_add_image), null, null)
    }
}