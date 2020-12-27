package com.raywenderlich.wewatch.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.interactor.Interactor
import com.raywenderlich.wewatch.domain.state.MovieState
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * @author Макеев Иван
 */
class SearchViewModel(private val movieInteractor: Interactor) : BaseViewModel() {

    private val confirmPublishSubject: PublishSubject<Movie> = PublishSubject.create<Movie>()
    private val moviePublishSubject: PublishSubject<Movie> = PublishSubject.create<Movie>()
    private val _stateLiveData = MutableLiveData<MovieState>()

    val stateLiveData: LiveData<MovieState> = _stateLiveData

    init {
        observeConfirmIntent()
        observeAddMovieIntent()
    }

    fun confirmIntent(movie: Movie) {
        confirmPublishSubject.onNext(movie)
    }

    fun displayMoviesIntent(title: String){
        observeMovieDisplayIntent(title)
    }

    fun addMovieIntent(movie: Movie){
        moviePublishSubject.onNext(movie)
    }

    private fun observeConfirmIntent() =
        confirmPublishSubject
            .doOnNext { Log.d(TAG, "SearchViewModel Intent: confirm") }
            .observeOn(Schedulers.io())
            .flatMap<MovieState>(movieInteractor::addMovie)
            .subscribe(_stateLiveData::postValue)
            .addTo(compositeDisposable)

    private fun observeAddMovieIntent() =
        moviePublishSubject
            .doOnNext { Log.d(TAG, "SearchViewModel Intent: add movie") }
            .map<MovieState>(MovieState::ConfirmationState)
            .subscribe(_stateLiveData::postValue)
            .addTo(compositeDisposable)

    private fun observeMovieDisplayIntent(title: String) =
        Observable.just(title)
            .doOnNext { Log.d(TAG, "SearchViewModel Intent: display movie") }
            .flatMap<MovieState>(movieInteractor::searchMovies)
            .subscribeOn(Schedulers.io())
            .startWith(MovieState.LoadingState)
            .subscribe(_stateLiveData::postValue)
            .addTo(compositeDisposable)

    private companion object{
        const val TAG = "MVI_Example"
    }
}