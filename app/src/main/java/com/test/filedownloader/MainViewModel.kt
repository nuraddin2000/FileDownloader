package com.test.filedownloader

import androidx.lifecycle.ViewModel
import com.test.filedownloader.model.DownloadResult
import com.test.filedownloader.repository.FileDownloaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FileDownloaderRepository) :
    ViewModel() {

    suspend fun downloadFile(file: File, url: String): Flow<DownloadResult> {
        return flow {
            var deleteFile = true
            try {
                emit(DownloadResult.Loading)
                val response = repository.downloadFile(url)
                val body = response.body()
                // flag to delete file if download errors or is cancelled
                body?.byteStream().use { inputStream ->
                    file.outputStream().use { outputStream ->
                        val totalBytes = response.body()!!.contentLength()
                        val data = ByteArray(totalBytes.toInt())
                        var progressBytes = 0L

                        while (true) {
                            val bytes = inputStream?.read(data)

                            if (bytes == -1) {
                                break
                            }

                            outputStream.write(data, 0, bytes!!)
                            progressBytes += bytes

                            emit(DownloadResult.Progress(((progressBytes * 100) / totalBytes).toInt()))
                        }

                        when {
                            progressBytes < totalBytes ->
                                throw Exception("missing bytes")
                            progressBytes > totalBytes ->
                                throw Exception("too many bytes")
                            else ->
                                deleteFile = false
                        }
                    }
                }

                emit(DownloadResult.Success)
            } catch (e: Exception) {
                emit(DownloadResult.Error(e.message!!))
            } finally {
                // check if download was successful
                if (deleteFile) {
                    file.delete()
                }
            }
        }
    }
}
