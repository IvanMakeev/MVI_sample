package com.raywenderlich.wewatch.view.reducer

import com.raywenderlich.wewatch.common.IResourceProvider
import com.raywenderlich.wewatch.domain.Reducer
import com.raywenderlich.wewatch.domain.action.MovieAction
import com.raywenderlich.wewatch.domain.state.MovieViewState

/**
 * @author Макеев Иван
 */
class MovieReducer(private val provider: IResourceProvider) : Reducer<MovieAction, MovieViewState> {

    /**
     * На вход получаем новые Action и текущий ViewState, далее на основании этого Action создаем новый ViewState,
     * либо не модифицирую ViewState возвращаем его.
     */
    override fun reduce(viewState: MovieViewState, action: MovieAction): MovieViewState =
        when (action) {
            is MovieAction.SearchMovieAction -> viewState
            is MovieAction.LoadedMovieAction -> MovieViewState.DataState(action.movies)
            is MovieAction.MovieErrorAction -> MovieViewState.ErrorState(provider.provideErrorMessage())
            is MovieAction.MovieDontFoundAction -> MovieViewState.MessageState(provider.provideDidntFindMoviesMessage())
            is MovieAction.ShowMessageAction -> MovieViewState.MessageState(action.message)
            is MovieAction.FinishAction -> MovieViewState.FinishState
            is MovieAction.ConfirmAction -> MovieViewState.ConfirmationState(action.movie)
            is MovieAction.LoadingAction -> MovieViewState.LoadingState
            is MovieAction.AddMovieAction, is MovieAction.DeleteMovieAction, is MovieAction.DisplayMovieAction -> viewState
            is MovieAction.DeletedMovieAction -> MovieViewState.MessageState(
                provider.provideDeleteMessage(action.moviesTitle)
            )
        }
}