package com.test.filedownloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.filedownloader.model.DownloadResult
import com.test.filedownloader.model.DummyData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.Exception

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var job: Job
    private var map: MutableMap<String, Job> = mutableMapOf()
    companion object {
        val data = arrayOf(
            DummyData("1", "Title1.pdf", "2015/icelandic/dictionary.pdf"),
            DummyData("2", "Title2.pdf", "2015/icelandic/dictionary.pdf"),
            DummyData("3", "Title3.pdf", "2015/icelandic/dictionary.pdf"),
            DummyData("4", "Title4.pdf", "2017/newsletter/drylab.pdf"),
            DummyData("5", "Title5.pdf", "2017/newsletter/drylab.pdf"),
            DummyData("6", "Title6.pdf", "2017/newsletter/drylab.pdf")
        )
    }

    lateinit var myAdapter: AttachmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            ).apply {
                addItemDecoration(this)
            }
            myAdapter = AttachmentAdapter(data)
            myAdapter.downloadfile = {
                try {
                    downloadWithFlow(it)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error while downloading ${it.title}", Toast.LENGTH_LONG).show()
                }
            }
            myAdapter.cancelDownloading = {
                val job = map[it.id]
                job?.cancel()
            }

            myAdapter.openFileListener = {
                openFile(it.file)
            }
            adapter = myAdapter
        }
    }

    private fun downloadWithFlow(dummy: DummyData) {
       job = CoroutineScope(Dispatchers.IO).launch {
           map[dummy.id] = job
            viewModel.downloadFile(dummy.file, dummy.url).collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is DownloadResult.Success -> {
                            myAdapter.setDownloading(dummy, false)
                        }

                        is DownloadResult.Loading -> {
                            myAdapter.setDownloading(dummy, true)
                        }
                        is DownloadResult.Error -> {
                            myAdapter.setDownloading(dummy, false)
                            map.remove(dummy.id)
                            Toast.makeText(this@MainActivity, "Error while downloading ${dummy.title}", Toast.LENGTH_LONG).show()
                        }
                        is DownloadResult.Progress -> {
                            myAdapter.setProgress(dummy, it.progress)
                        }
                    }
                }
            }
        }
    }
}