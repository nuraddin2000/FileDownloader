package com.test.filedownloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.test.filedownloader.model.DummyData
import kotlinx.android.synthetic.main.item_list.view.*


class AttachmentAdapter(var dummyes: Array<DummyData>) : RecyclerView.Adapter<ViewHolder>() {

    var cancelDownloading: ((DummyData) -> Unit)? = null
    var downloadfile: ((DummyData) -> Unit)? = null
    var openFileListener: ((DummyData) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun getItemCount(): Int = dummyes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dummy = dummyes[position]
        with(holder.itemView) {
            title.text = dummy.title
            downloadIcon.isVisible = !dummy.file.exists()
            documentTypeIcon.isVisible = dummy.file.exists()
            progressBarDocument.isVisible = dummy.isDownloading
            textProgress.isVisible = dummy.isDownloading
            retry_or_cancel_button.isVisible = !dummy.file.exists()
            if(dummy.isDownloading) {
                retry_or_cancel_button.text = "Cancel"
            }else {
                retry_or_cancel_button.text = "Download"
            }
            retry_or_cancel_button.setOnClickListener {
                if(dummy.isDownloading) {
                    dummy.isDownloading = false
                    dummy.progress = 0
                    cancelDownloading?.invoke(dummy)
                    progressBarDocument.progress = 0
                    textProgress.text = "Loading..."
                    notifyItemChanged(dummyes.indexOf(dummy))
                }else {
                    downloadfile?.invoke(dummy)
                }

            }
            setOnClickListener {
                if(dummy.file.exists()) {
                    openFileListener?.invoke(dummy)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with(holder.itemView) {
                (payloads.first() as Bundle).getInt("progress").also {
                    progressBarDocument.progress = it
                    progressBarDocument.isVisible = it < 99
                    textProgress.isVisible = it < 99
                    textProgress.text = "$it %"
                }
            }
        }
    }

    fun setDownloading(dummy: DummyData, isDownloading: Boolean) {
        getDummy(dummy)?.isDownloading = isDownloading
        notifyItemChanged(dummyes.indexOf(dummy))
    }

    fun setProgress(dummy: DummyData, progress: Int) {
        getDummy(dummy)?.progress = progress
        notifyItemChanged(dummyes.indexOf(dummy), Bundle().apply { putInt("progress", progress) })
    }

    private fun getDummy(dummy: DummyData) = dummyes.find { dummy.id == it.id }


}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
