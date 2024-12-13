package com.funshow.rtmp_rootencoder_rotation

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File

//路徑工具
object PathUtils {
    //取得記錄路徑
    @JvmStatic
    fun getRecordPath(): File {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        return File(storageDir.absolutePath + "/RootEncoder")
    }

    //更新圖庫
    @JvmStatic
    fun updateGallery(context: Context, path: String) {
        MediaScannerConnection.scanFile(context, arrayOf(path), null, null)
        context.toast("Video saved at: $path")
    }
}
