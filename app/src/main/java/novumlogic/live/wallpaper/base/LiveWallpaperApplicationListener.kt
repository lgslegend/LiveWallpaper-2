package novumlogic.live.wallpaper.base

import android.content.Context
import android.text.TextUtils
import android.util.Log
import novumlogic.live.wallpaper.backends.AndroidWallpaperListener
import novumlogic.live.wallpaper.gdx.ApplicationListener
import novumlogic.live.wallpaper.gdx.Gdx
import novumlogic.live.wallpaper.gdx.InputProcessor
import novumlogic.live.wallpaper.gdx.audio.Music
import novumlogic.live.wallpaper.gdx.graphics.Camera
import novumlogic.live.wallpaper.gdx.graphics.GL20
import novumlogic.live.wallpaper.gdx.graphics.OrthographicCamera
import novumlogic.live.wallpaper.gdx.graphics.Texture
import novumlogic.live.wallpaper.gdx.math.Vector3
import novumlogic.live.wallpaper.utility.*
import java.io.File
import java.util.*
import javax.microedition.khronos.opengles.GL10
import kotlin.collections.ArrayList

/**
 * Created by Priya Sindkar.
 */
class LiveWallpaperApplicationListener(val mContext: Context) : ApplicationListener, InputProcessor, AndroidWallpaperListener {

    private var mMusic: Music? = null
    private var waterRipples: WaterRipples? = null
    private var camera: Camera? = null
    private var gridDims: Vector3? = null

    private var backgroundTexture: Texture? = null
    private var task: TimerTask? = null
    private var timer: Timer? = null

    private var xTouchDown = 0
    private var yTouchDown = 0
    private var elapsedSeconds = 0f
    private var randomWallpaperTimeCounter = -1

    override fun create() {
        Gdx.input.inputProcessor = this
        mMusic = Gdx.audio.newMusic(Gdx.files.internal("aquarium.mp3"))
        mMusic!!.volume = 0.5f
        mMusic!!.isLooping = true
        playBackgroundScore()
        camera = OrthographicCamera()
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val deltaSeconds = Gdx.graphics.deltaTime
        elapsedSeconds += deltaSeconds

        // change wallpaper every 2 mins
        if (mContext.fetchWallpaperType() == AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId) {
            if (elapsedSeconds >= 86400) {
                renderNextWallpaper(mContext.fetchAutoWallpaperGalleryList())
                elapsedSeconds = 0f
            }
        } else if (mContext.fetchWallpaperType() == AppConstants.WallpaperType.AUTO_ROTATE_ONLINE_IMAGE.typeId) {
            if (elapsedSeconds >= 86400) {
                renderNextWallpaper(mContext.fetchAutoWallpaperOnlineImageList())
                elapsedSeconds = 0f
            }
        }
        if (waterRipples != null) {
            Gdx.gl20.glActiveTexture(GL10.GL_TEXTURE)
            Gdx.gl20.glEnable(GL10.GL_TEXTURE_2D)
            backgroundTexture!!.bind()
            waterRipples!!.render(camera, false)
        }
    }

    override fun resize(width: Int, height: Int) {
        waterRipples = null

        var filePath = ""

        // check for deleted gallery images

        if (mContext.fetchWallpaperType() == AppConstants.WallpaperType.GALLERY.typeId) {
            if (!ImageUtils.checkIfFileExists(mContext.fetchImageToPreview())) {
                mContext.removeImageToPreview()
                mContext.removeWallpaperType()
            }
        } else if (mContext.fetchWallpaperType() == AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId) {
            val listOfGalleryImages = mContext.fetchAutoWallpaperGalleryList()
            val existingImages= listOfGalleryImages.filter { ImageUtils.checkIfFileExists(it) } as ArrayList<String>

            if (existingImages.isEmpty()) { // if no wallpaper from the list exists, remove preferences
                mContext.removeImageToPreview()
                mContext.removeWallpaperType()
                mContext.removeAutoWallpaperGalleryList()
            } else {
                mContext.saveAutoWallpaperGalleryList(existingImages)
            }
        }

        if (mContext.fetchWallpaperType() == AppConstants.WallpaperType.AUTO_ROTATE_GALLERY.typeId) {
            val galleryImages = mContext.fetchAutoWallpaperGalleryList()
            if (galleryImages.isNotEmpty()) {
                filePath = galleryImages[0]
            }

        } else if (mContext.fetchWallpaperType() == AppConstants.WallpaperType.AUTO_ROTATE_ONLINE_IMAGE.typeId) {
            val onlineImages = mContext.fetchAutoWallpaperOnlineImageList()
            if (onlineImages.isNotEmpty()) {
                filePath = onlineImages[0]
            }
        } else {
            filePath = mContext.fetchImageToPreview()
        }

        gridDims = resetCamera(width, height)
        if (gridDims == null) {
            return
        }

        setWatterRipplesData(filePath)

        // start showing random ripples
        if (waterRipples != null) {
            if (mContext.fetchRainDropSetting()) { // to show first drop immediately on preview display
                val randomWidth = Random().nextInt(Gdx.graphics.width)
                val randomHeight = Random().nextInt(Gdx.graphics.height)
                waterRipples!!.touchScreen(camera, randomWidth, randomHeight)
            }
            randomWaterRipple(mContext.fetchRainDropSetting())
        }
    }

    private fun resetCamera(width: Int, height: Int): Vector3? {
        camera = null

        if (width < 200 || height < 200) {
            return null
        }

        val gridWidth = (width / WaterRipples.CellSuggestedDimension).toFloat()
        val gridHeight = (height / WaterRipples.CellSuggestedDimension).toFloat()

        var gridZ = 0f
        camera = OrthographicCamera(gridWidth, gridHeight)

        val cameraX = gridWidth / 2f

        val cameraY = gridHeight / 2f
        val cameraZ = 0f

        camera!!.position.set(cameraX, cameraY, cameraZ)
        gridZ = camera!!.position.z - (camera!!.near + (camera!!.far - camera!!.near) / 5f)
        camera!!.update()

        return Vector3(gridWidth, gridHeight, gridZ)
    }

    private fun setWatterRipplesData(fileName: String) {
        val file = if (fileName.isEmpty()) {
            Gdx.files.internal("default_image.jpg")
        } else {
            if (fileName.startsWith("/data")) {
                Gdx.files.absolute(File(fileName).path)
            } else {
                Gdx.files.absolute(fileName)
            }
        }

        backgroundTexture = Texture(file)
        backgroundTexture!!.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        backgroundTexture!!.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)

        waterRipples = WaterRipples(gridDims!!.z, 0f, 0f, gridDims!!.x.toInt(), gridDims!!.y.toInt(), backgroundTexture)
    }

    override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
        if (waterRipples != null) {
            if (mContext.fetchRippleSetting()) {
                waterRipples!!.touchScreen(camera, x, y)
            }
        }
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        if (waterRipples != null) {
            if (mContext.fetchRippleSetting()) {
                waterRipples!!.touchScreen(camera, x, y)
            }
        }
        if (pointer == 0) {
            xTouchDown = x
            yTouchDown = y
        }
        return false
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
        if (pointer == 0) {
            if (xTouchDown == x && yTouchDown == y) {
                val gridDims = resetCamera(Gdx.graphics.width, Gdx.graphics.height)
                if (gridDims != null) {
                    waterRipples!!.updateZ(gridDims.z)
                }
            }
        }
        return false
    }

    private fun randomWaterRipple(showRipple: Boolean) {
        if (showRipple) {
            if (this.timer == null) {
                this.timer = Timer()
                this.task = object : TimerTask() {
                    override fun run() {
                        if (showRipple) {
                            if (waterRipples != null) {
                                val randomWidth = Random().nextInt(Gdx.graphics.width)
                                val randomHeight = Random().nextInt(Gdx.graphics.height)
                                waterRipples!!.touchScreen(camera, randomWidth, randomHeight)
                            }
                        }
                    }
                }
                this.timer!!.schedule(this.task, 4000, 4000)
            }
        } else if (this.timer != null) {
            this.timer!!.cancel()
            this.task!!.cancel()
            this.timer = null
            this.task = null
        }
    }

    private fun playBackgroundScore() {
        if (mContext.fetchBackgroundMusicSetting()) {
            if (!mMusic!!.isPlaying) {
                mMusic!!.play()
            }
        } else {
            if (mMusic!!.isPlaying) {
                mMusic!!.stop()
            }
        }
    }

    private fun renderNextWallpaper(listOfImages: ArrayList<String>) {
        val filePath = getNext(listOfImages)
        if (!TextUtils.isEmpty(filePath)) {
            setWatterRipplesData(filePath)
        } else {
            renderNextWallpaper(listOfImages)
        }
    }

    private fun getNext(listOfImages: ArrayList<String>): String {
        randomWallpaperTimeCounter++
        if (randomWallpaperTimeCounter >= listOfImages.size) {
            randomWallpaperTimeCounter = 0
        }

        return listOfImages[randomWallpaperTimeCounter]
    }

    private fun moveCameraX(offSet: Float) {
        if (offSet > 0) {
            this.camera!!.position.x = offSet
            this.camera!!.update()
        }
    }

    override fun dispose() {
        if (backgroundTexture != null) {
            backgroundTexture!!.dispose()
        }

        if (mMusic != null) {
            mMusic!!.dispose()
        }
    }

    override fun pause() {}

    override fun resume() {
        if (mMusic != null)
            playBackgroundScore()
    }

    override fun keyDown(arg0: Int): Boolean {
        return false
    }

    override fun keyTyped(arg0: Char): Boolean {
        return false
    }

    override fun keyUp(arg0: Int): Boolean {
        return false
    }

    override fun scrolled(arg0: Int): Boolean {
        return false
    }

    override fun offsetChange(xOffset: Float, yOffset: Float, xOffsetStep: Float,
                              yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
        Log.d("OFFSET", "xPixelOffset $xPixelOffset")
        //        moveCameraX(-(xPixelOffset / 30));
    }

    override fun previewStateChange(isPreview: Boolean) {}

    override fun iconDropped(x: Int, y: Int) {}
}