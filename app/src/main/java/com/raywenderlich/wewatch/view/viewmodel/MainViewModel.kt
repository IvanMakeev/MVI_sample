package com.raywenderlich.wewatch.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.domain.interactor.Interactor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * @author Макеев Иван
 */
class MainViewModel(private val movieInteractor: Interactor): BaseViewModel()  {

    private val movePublishSubject: PublishSubject<Movie> = PublishSubject.create()
    private val _stateLiveData = MutableLiveData<MovieState>()

    val stateLiveData: LiveData<MovieState> = _stateLiveData

    init {
        observeMovieDeleteIntent()
        observeMovieDisplay()
    }

    fun deleteMovieIntent(movie: Movie){
        movePublishSubject.onNext(movie)
    }

    private fun observeMovieDeleteIntent() =
        movePublishSubject
            .doOnNext { Log.d(TAG, "MainViewModel Intent: delete movie") }
            .observeOn(Schedulers.io())
            .flatMap<Unit>(movieInteractor::deleteMovie)
            .subscribe()

    private fun observeMovieDisplay() =
        movieInteractor.getMovieList()
            .doOnNext { Log.d(TAG, "MainViewModel Intent: display movie") }
            .startWith(MovieState.LoadingState)
            .subscribe(_stateLiveData::postValue)

    private companion object{
        const val TAG = "MVI_Example"
    }
}