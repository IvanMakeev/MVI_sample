package com.raywenderlich.wewatch.view.presenter

import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.domain.interactor.Interactor
import com.raywenderlich.wewatch.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Макеев Иван
 */
class MainPresenter(private val movieInteractor: Interactor) {

    private lateinit var view: MainView
    private val compositeDisposable = CompositeDisposable()

    fun bind(view: MainView) {
        this.view = view
        compositeDisposable.apply {
            add(observeMovieDeleteIntent())
            add(observeMovieDisplay())
        }
    }

    fun unbind() {
        if (compositeDisposable.isDisposed.not()) {
            compositeDisposable.dispose()
        }
    }

    private fun observeMovieDeleteIntent() =
        view.deleteMovieIntent()
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .flatMap<Unit>(movieInteractor::deleteMovie)
            .subscribe()

    private fun observeMovieDisplay() =
        movieInteractor.getMovieList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view.render(MovieState.LoadingState) }
            .doOnNext(view::render)
            .subscribe()
}