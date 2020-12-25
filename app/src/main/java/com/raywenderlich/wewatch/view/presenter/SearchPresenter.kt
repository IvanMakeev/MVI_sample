package com.raywenderlich.wewatch.view.presenter

import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.domain.interactor.Interactor
import com.raywenderlich.wewatch.view.SearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Макеев Иван
 */
class SearchPresenter(private val movieInteractor: Interactor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: SearchView

    fun bind(view: SearchView) {
        this.view = view
        compositeDisposable.apply {
            add(observeMovieDisplayIntent())
            add(observeAddMovieIntent())
            add(observeConfirmIntent())
        }
    }

    fun unbind() {
        if (compositeDisposable.isDisposed.not()) {
            compositeDisposable.dispose()
        }
    }

    private fun observeConfirmIntent() =
        view.confirmIntent()
            .observeOn(Schedulers.io())
            .flatMap<MovieState>(movieInteractor::addMovie)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::render)

    private fun observeAddMovieIntent() =
        view.addMovieIntent()
            .map<MovieState>(MovieState::ConfirmationState)
            .subscribe(view::render)

    private fun observeMovieDisplayIntent() =
        view.displayMoviesIntent()
            .flatMap<MovieState>(movieInteractor::searchMovies)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.render(MovieState.LoadingState) }
            .subscribe(view::render)
}