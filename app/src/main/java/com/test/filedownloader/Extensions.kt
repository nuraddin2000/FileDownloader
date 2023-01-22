package com.test.filedownloader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File

val globalContext: Context
    get() = App.appContext


fun Activity.openFile(file: File) {
    Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        addCategory(Intent.CATEGORY_DEFAULT)
        val uri = FileProvider.getUriForFile(this@openFile, BuildConfig.APPLICATION_ID + ".provider", file)
        val mimeType = getMimeType(file)
        mimeType?.let {
            setDataAndType(uri, it)
            startActivity(this)
        }

    }
}

fun getMimeType(file: File): String? {
    val extension = file.extension
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}