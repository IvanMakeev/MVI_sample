package com.raywenderlich.wewatch.domain.interactor

import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.domain.state.MovieState
import com.raywenderlich.wewatch.domain.repository.Repository
import io.reactivex.Observable

/**
 * @author Макеев Иван
 */
class MovieInteractor(private val repository: Repository) : Interactor {

    override fun getMovieList(): Observable<MovieState> =
        repository.getMovieList()
            .map<MovieState> { MovieState.DataState(it) }
            .onErrorReturn { MovieState.ErrorState("Error") }

    override fun addMovie(movie: Movie): Observable<MovieState> =
        repository.addMovie(movie)
            .map<MovieState> { MovieState.FinishState }
            .toObservable()

    override fun deleteMovie(movie: Movie): Observable<Unit> =
        repository.deleteMovie(movie)

    override fun searchMovies(title: String): Observable<MovieState> =
        repository.searchMovies(title)
            .map<MovieState> { response ->
                response.results?.let { MovieState.DataState(it) }
            }.onErrorReturn { MovieState.ErrorState("Error") }
}