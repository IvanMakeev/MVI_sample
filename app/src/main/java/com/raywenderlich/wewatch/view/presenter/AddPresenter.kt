package com.raywenderlich.wewatch.view.presenter

import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.domain.interactor.Interactor
import com.raywenderlich.wewatch.view.AddView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Макеев Иван
 */
class AddPresenter(private val movieInteractor: Interactor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: AddView

    fun bind(view: AddView) {
        this.view = view
        compositeDisposable.add(observeAddMovieIntent())
    }

    fun unbind() {
        if (compositeDisposable.isDisposed.not()) {
            compositeDisposable.dispose()
        }
    }

    private fun observeAddMovieIntent(): Disposable =
        view.addMovieIntent()
            .observeOn(Schedulers.io())
            .flatMap<MovieState>(movieInteractor::addMovie)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::render)
}