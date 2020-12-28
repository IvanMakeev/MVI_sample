package com.raywenderlich.wewatch.domain.middleware

import android.util.Log
import com.raywenderlich.wewatch.domain.MiddleWare
import com.raywenderlich.wewatch.domain.action.MovieAction
import com.raywenderlich.wewatch.domain.repository.Repository
import com.raywenderlich.wewatch.domain.state.MovieViewState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * @author Макеев Иван
 */
class MainMiddleWare(private val repository: Repository) :
    MiddleWare<MovieAction, MovieViewState>() {

    override fun bind(action: Observable<MovieAction>): Observable<MovieAction> =
        action
            .filter(::canContinue)
            .flatMap { actions ->
                Log.d(TAG, "MainMiddleWare Action: ${actions.javaClass.simpleName}")
                when (actions) {
                    is MovieAction.DisplayMovieAction -> displayMovie()
                    is MovieAction.DeleteMovieAction -> deleteMovie(actions)
                    else -> throw IllegalStateException("Action not processed")
                }
            }

    private fun displayMovie(): Observable<MovieAction> =
        repository.getMovieList()
            .map<MovieAction>(MovieAction::LoadedMovieAction)
            .onErrorReturn { throwable -> MovieAction.MovieErrorAction(throwable) }
            .subscribeOn(Schedulers.io())
            .startWith(MovieAction.LoadingAction)

    private fun deleteMovie(action: MovieAction.DeleteMovieAction): Observable<MovieAction> =
        repository.deleteMovie(action.movie)
            .map<MovieAction> { MovieAction.DeletedMovieAction(action.movie.title.orEmpty()) }
            .onErrorReturn { throwable -> MovieAction.MovieErrorAction(throwable) }
            .subscribeOn(Schedulers.io())

    private fun canContinue(action: MovieAction) =
        action is MovieAction.DisplayMovieAction
                || action is MovieAction.DeleteMovieAction

    private companion object {
        const val TAG = "MVI_Example"
    }
}