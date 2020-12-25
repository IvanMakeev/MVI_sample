package com.raywenderlich.wewatch.domain.repository

import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.data.model.MoviesResponse
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author Макеев Иван
 */
interface Repository {
    fun getMovieList(): Observable<List<Movie>>
    fun deleteMovie(movie: Movie): Observable<Unit>
    fun searchMovies(title: String): Observable<MoviesResponse>
    fun addMovie(movie: Movie): Single<Long>
}