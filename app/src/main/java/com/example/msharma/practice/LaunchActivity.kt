package com.example.msharma.practice

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.example.msharma.practice.R.*
import com.example.msharma.practice.adapters.LOADING_VIEW
import com.example.msharma.practice.adapters.PHOTO_VIEW
import com.example.msharma.practice.adapters.PhotoAdapter
import com.example.msharma.practice.network.PhotoService
import com.example.msharma.practice.ui.GridItemDecoration
import com.example.msharma.practice.viewmodel.LaunchActivityViewModel
import com.example.msharma.practice.viewmodelfactory.LaunchViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_launch.*
import javax.inject.Inject

private const val TAG = "LaunchActivity"
class LaunchActivity : DaggerAppCompatActivity() {

    private val bin: CompositeDisposable by lazy {
        CompositeDisposable()
    }


    private fun Disposable.into(bin: CompositeDisposable) {
        bin.add(this)
    }

    private val adapter = PhotoAdapter()

    @Inject
    lateinit var photoService: PhotoService

    private val viewModel: LaunchActivityViewModel by lazy {
        val viewModel = ViewModelProviders.of(this, LaunchViewModelFactory(application, photoService))
                .get(LaunchActivityViewModel::class.java)
        viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layout.activity_launch)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = (object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    PHOTO_VIEW -> 1
                    LOADING_VIEW -> 3
                    else -> 0
                }
            }
        })
        val itemDecoration = GridItemDecoration(applicationContext)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(scrollListener)

        viewModel.showLoading.subscribe { isVisible ->
            progressbar.visibility = if (isVisible) View.VISIBLE else View.GONE
        }.into(bin)

        viewModel.emptyList.subscribe {
            Toast.makeText(this, getString(string.no_photo_msg), Toast.LENGTH_SHORT).show()
        }.into(bin)

        viewModel.errorMsg.subscribe { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }.into(bin)

        viewModel.photoThumbnailListSubject.subscribe { urlList ->
            Log.v(TAG, "list of url is ${urlList.first.size} and show loading ${urlList.second}")
            adapter.setItems(urlList)
        }.into(bin)

        viewModel.clearData.subscribe {
            adapter.clearItems()
        }.into(bin)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val totalItemCount = recyclerView.layoutManager.itemCount
            val lastVisibleItem = (recyclerView.layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
            viewModel.needMoreData.onNext((lastVisibleItem.toFloat() / totalItemCount))
            Log.v(TAG, "Item count is $totalItemCount and last visible item is $lastVisibleItem")
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val searchItem = menu.findItem(id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setQuery(viewModel.photoSearchKeyword.value, false)
        searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.photoSearchKeyword.onNext(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        searchView.isIconified = false
        return true
    }

}
