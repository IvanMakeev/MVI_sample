package com.raywenderlich.wewatch.domain.middleware

import android.util.Log
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.MiddleWare
import com.raywenderlich.wewatch.domain.action.MovieAction
import com.raywenderlich.wewatch.domain.repository.Repository
import com.raywenderlich.wewatch.domain.state.MovieViewState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * @author Макеев Иван
 */
class SearchMiddleWare(private val repository: Repository) :
    MiddleWare<MovieAction, MovieViewState>() {

    override fun bind(action: Observable<MovieAction>): Observable<MovieAction> =
        action
            .filter(::canContinue)
            .flatMap { actions ->
                Log.d(TAG, "SearchMiddleWare Action: ${actions.javaClass.simpleName}")
                when (actions) {
                    is MovieAction.SearchMovieAction -> searchMovie(actions)
                    is MovieAction.AddMovieAction -> addMovie(actions)
                    else -> throw IllegalStateException("Action not processed")
                }
            }

    private fun searchMovie(action: MovieAction.SearchMovieAction): Observable<MovieAction> =
        repository.searchMovies(action.title)
            .map { convertToResult(it.results) }
            .onErrorReturn { throwable -> MovieAction.MovieErrorAction(throwable) }
            .subscribeOn(Schedulers.io())
            .startWith(MovieAction.LoadingAction)

    private fun convertToResult(movies: List<Movie>?): MovieAction {
        return if (movies.isNullOrEmpty()) {
            MovieAction.MovieDontFoundAction
        } else {
            MovieAction.LoadedMovieAction(movies)
        }
    }

    private fun addMovie(action: MovieAction.AddMovieAction): Observable<MovieAction> =
        repository.addMovie(action.movie)
            .map<MovieAction> { MovieAction.FinishAction }
            .onErrorReturn { throwable -> MovieAction.MovieErrorAction(throwable) }
            .subscribeOn(Schedulers.io())

    private fun canContinue(action: MovieAction) =
        action is MovieAction.SearchMovieAction
                || action is MovieAction.AddMovieAction

    private companion object {
        const val TAG = "MVI_Example"
    }
}