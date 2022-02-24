package gapara.co.id.core.base

import android.app.Activity
import android.content.Context
import android.content.Intent

class GalleryHelper(private val context: Context) {

    fun showGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/"
        (context as Activity).startActivityForResult(Intent.createChooser(intent, "Cari Gambar"), ARG_GALLERY)
    }

    fun showVideo() {
        val intent = Intent()
        intent.type = "video/mp4"
        intent.action = Intent.ACTION_GET_CONTENT
        (context as Activity).startActivityForResult(Intent.createChooser(intent, "Cari Video"), ARG_VIDEO)
    }

    companion object {
        var ARG_GALLERY = 64
        var ARG_VIDEO = 65
    }
}