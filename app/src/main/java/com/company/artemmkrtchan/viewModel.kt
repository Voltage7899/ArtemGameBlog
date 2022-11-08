package com.company.artemmkrtchan

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class viewModel :ViewModel() {

    val liveDataCurrentUser=MutableLiveData<Uri>()

}