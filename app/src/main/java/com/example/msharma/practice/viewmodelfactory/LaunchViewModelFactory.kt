package com.example.msharma.practice.viewmodelfactory

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.msharma.practice.network.PhotoService
import com.example.msharma.practice.viewmodel.LaunchActivityViewModel

class LaunchViewModelFactory(private val application: Application, private val photoService: PhotoService) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LaunchActivityViewModel(application, photoService) as T
    }
}
