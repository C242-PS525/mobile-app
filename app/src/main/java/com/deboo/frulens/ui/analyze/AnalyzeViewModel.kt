package com.deboo.frulens.ui.analyze

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deboo.frulens.data.Serving

class AnalyzeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Try to upload fruit image and press 'Analyze' button"
    }
    val text: LiveData<String> = _text
    var currentImageUri: Uri? = null
    var servedata:String = ""
    var identification = ""
    var otherservedata: List<Serving> = emptyList()
}