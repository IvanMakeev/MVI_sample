package com.raywenderlich.wewatch.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.interactor.Interactor
import com.raywenderlich.wewatch.domain.state.MovieState
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * @author Макеев Иван
 */
class AddViewModel(private val movieInteractor: Interactor) : BaseViewModel() {

    private val movePublishSubject: PublishSubject<Movie> = PublishSubject.create()
    private val messagePublishSubject: PublishSubject<MovieState> = PublishSubject.create()
    private val _stateLiveData = MutableLiveData<MovieState>()

    val stateLiveData: LiveData<MovieState> = _stateLiveData

    init {
        observeAddMovieIntent()
        observeShowMessageIntent()
    }

    fun addMovieIntent(movie: Movie) {
        movePublishSubject.onNext(movie)
    }

    fun showMessageIntent(string: String){
        messagePublishSubject.onNext(MovieState.ErrorState(string))
    }

    private fun observeAddMovieIntent(): Disposable =
        movePublishSubject
            .doOnNext { Log.d(TAG, "SearchViewModel Intent: add movie") }
            .observeOn(Schedulers.io())
            .flatMap<MovieState>(movieInteractor::addMovie)
            .subscribe(_stateLiveData::postValue)
            .addTo(compositeDisposable)

    private fun observeShowMessageIntent(): Disposable =
        messagePublishSubject
            .doOnNext { Log.d(TAG, "SearchViewModel Intent: show message") }
            .observeOn(Schedulers.io())
            .subscribe(_stateLiveData::postValue)
            .addTo(compositeDisposable)


    private companion object{
        const val TAG = "MVI_Example"
    }
}