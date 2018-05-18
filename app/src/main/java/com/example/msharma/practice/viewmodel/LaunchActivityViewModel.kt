package com.example.msharma.practice.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.util.Log
import com.example.msharma.practice.data.Photo
import com.example.msharma.practice.network.PhotoService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private const val TAG = "LaunchActivityViewModel"
// cut off when we start downloading more data
private const val MORE_DATA_DOWNLOAD = 0.7

class LaunchActivityViewModel(application: Application, private val photoService: PhotoService) : AndroidViewModel(application) {

    private val bin: CompositeDisposable by lazy {
        CompositeDisposable()
    }


    private fun Disposable.into(bin: CompositeDisposable) {
        bin.add(this)
    }

    private val photoArrayList = ArrayList<Photo>()
    private var currentPageNumber = 1
    var needMoreData: PublishSubject<Float> = PublishSubject.create()
    val photoThumbnailListSubject: BehaviorSubject<Pair<List<String>, Boolean>> = BehaviorSubject.create()
    val photoSearchKeyword: BehaviorSubject<String> = BehaviorSubject.create()
    val clearData: PublishSubject<Unit> = PublishSubject.create()
    val showLoading: PublishSubject<Boolean> = PublishSubject.create()
    val emptyList: PublishSubject<Unit> = PublishSubject.create()
    //TODO though showing toast only now but we can use in case something is going wrong connection and inform users
    val errorMsg: PublishSubject<String> = PublishSubject.create()
    private var isDownloadingData = false

    init {
        needMoreData.subscribe { scrolledPercentage ->
            Log.v(TAG, "User scrolled to $scrolledPercentage")
            if (scrolledPercentage > MORE_DATA_DOWNLOAD && !isDownloadingData) {
                isDownloadingData = true
                currentPageNumber++
                getPhotos()
            }
        }.into(bin)

        photoSearchKeyword.distinctUntilChanged().subscribe {
            currentPageNumber = 1
            clearData.onNext(Unit)
            getPhotos()
        }
    }

    private fun getPhotos() {
        isDownloadingData = true
        showLoading.onNext(currentPageNumber == 1)
        photoService.getPhotosByKeyword(currentPageNumber, photoSearchKeyword.value)
                .subscribeOn(Schedulers.io())
                .map { photosResponse ->
                    val photoUrlList = ArrayList<String>()
                    photoArrayList.addAll(photosResponse.photos.photoList)
                    photosResponse.photos.photoList.forEach {
                        photoUrlList.add(it.thumbnailUrl)
                    }
                    //TODO for test loading first 10 pages
            //        Pair(photoUrlList, currentPageNumber <= 10)
                    Pair(photoUrlList, currentPageNumber <= photosResponse.photos.totalPage)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ photoList ->
                    showLoading.onNext(false)
                    if (photoList.first.isNotEmpty()) {
                        photoThumbnailListSubject.onNext(photoList)
                        if (photoList.second) {
                            isDownloadingData = false
                        }
                    } else {
                        emptyList.onNext(Unit)
                    }

                }, { error ->
                    // TODO handle error
                    Log.v(TAG, "on Error called ${error.message ?: ""}")
                    errorMsg.onNext(error?.message ?: "")
                //    throw Exceptions.propagate(Exception(error.message ?: ""))
                }).into(bin)
    }

    override fun onCleared() {
        super.onCleared()
        bin.dispose()
    }

}