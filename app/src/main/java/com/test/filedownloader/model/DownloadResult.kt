package com.test.filedownloader.model

sealed class DownloadResult {

    object Success : DownloadResult()

    object Loading : DownloadResult()

    data class Error(val message: String, val cause: Exception? = null) : DownloadResult()

    data class Progress(val progress: Int): DownloadResult()
}
