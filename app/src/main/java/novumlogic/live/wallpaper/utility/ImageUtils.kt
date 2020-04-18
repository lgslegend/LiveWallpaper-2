package novumlogic.live.wallpaper.utility

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Priya Sindkar.
 */
class ImageUtils {
    companion object {
        fun saveToInternalStorage(context: Context, imageURI: String, fileName: String): String {
            val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getDir(AppConstants.INTERNAL_IMAGE_DIR, Context.MODE_PRIVATE)
            // Create imageDir
            val internalDataFilePath = File(directory, fileName)

            if (internalDataFilePath.exists()) {
                context.saveImageToPreview(internalDataFilePath.absolutePath)
                return internalDataFilePath.absolutePath
            } else {
//                val imageUrl = URL(imageURI)
//                val conn = imageUrl.openConnection() as HttpURLConnection
//                conn.connectTimeout = 30000
//                conn.readTimeout = 30000
//                conn.instanceFollowRedirects = true
               // val `in`: InputStream = getContentResolver().openInputStream("your_uri_here")
                val inputStream = context.getContentResolver().openInputStream(Uri.parse(imageURI))
                val fileOutputStream = FileOutputStream(internalDataFilePath)
                val bufferSize = 16 * 1024
                try {
                    val bytes = ByteArray(bufferSize)
                    while (true) {
                        val count = inputStream.read(bytes, 0, bufferSize)
                        if (count == -1)
                            break
                        fileOutputStream.write(bytes, 0, count)
                    }



                    context.saveImageToPreview(internalDataFilePath.absolutePath)
                    return internalDataFilePath.absolutePath
                } catch (ex: Exception) {

                }
                fileOutputStream.close()
            }
            return ""
        }

        fun loadFilePathFromStorage(context: Context, fileName: String): String {
            if (!fileName.isNullOrEmpty()) {
                return try {
                    val contextWrapper = ContextWrapper(context)
                    val internalDataFilePath = contextWrapper.getDir(AppConstants.INTERNAL_IMAGE_DIR, Context.MODE_PRIVATE)
                    val imageFile = File(internalDataFilePath, fileName)
                    if (imageFile.exists()) {
                        imageFile.absolutePath
                    } else ""
                } catch (e: FileNotFoundException) {
                    ""
                }
            }
            return ""
        }

        fun checkIfFileExists(filePath : String): Boolean {
            val file = File(filePath)
            return file.exists()
        }
    }
}