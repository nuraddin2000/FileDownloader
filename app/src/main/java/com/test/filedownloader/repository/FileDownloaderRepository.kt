package com.test.filedownloader.repository

import com.test.filedownloader.api.ApiService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class FileDownloaderRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun downloadFile(fileUrl:String): Response<ResponseBody> {
        return apiService.downloadFile(fileUrl)
    }
}