package com.raywenderlich.wewatch.domain.action

import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.Action

/**
 * @author Макеев Иван
 */
sealed class MovieAction : Action {
    data class SearchMovieAction(val title: String) : MovieAction()
    data class ConfirmAction(val movie: Movie) : MovieAction()
    data class AddMovieAction(val movie: Movie) : MovieAction()
    data class DeleteMovieAction(val movie: Movie) : MovieAction()
    data class LoadedMovieAction(val movies: List<Movie>) : MovieAction()
    data class DeletedMovieAction(val moviesTitle: String) : MovieAction()
    data class MovieErrorAction(val throwable: Throwable?) : MovieAction()
    data class ShowMessageAction(val message: String) : MovieAction()
    object DisplayMovieAction : MovieAction()
    object LoadingAction : MovieAction()
    object MovieDontFoundAction : MovieAction()
    object FinishAction : MovieAction()
}