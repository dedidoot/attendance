package gapara.co.id.core.base.camera

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun createFile(context: Context, extension: String = ".jpg"): File {
  val formatter = SimpleDateFormat("dd_MMMM_yyyy_HH_mm_ss", Locale("id"))
  formatter.calendar = Calendar.getInstance()
  val fileName = formatter.format(Date()) + extension
  val directory = context.getDir("files", Context.MODE_PRIVATE)
  return File(directory, fileName)
}

fun convertBitmap(context: Context, bitmap: Bitmap, quality : Int = 50): Bitmap {
  val outStream = FileOutputStream(createFile(context))
  bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outStream)
  return bitmap
}