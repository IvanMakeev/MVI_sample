package com.raywenderlich.wewatch.data.repository

import com.raywenderlich.wewatch.data.db.MovieDao
import com.raywenderlich.wewatch.data.model.Movie
import com.raywenderlich.wewatch.data.model.MoviesResponse
import com.raywenderlich.wewatch.data.net.RetrofitClient
import com.raywenderlich.wewatch.domain.repository.Repository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * @author Макеев Иван
 */
class MovieRepository(
    private val remoteStorage: RetrofitClient,
    private val localStorage: MovieDao
) : Repository {
    override fun getMovieList(): Observable<List<Movie>> =
        localStorage.getAll()

    override fun addMovie(movie: Movie): Observable<Long> =
        localStorage.insert(movie).toObservable()

    override fun deleteMovie(movie: Movie): Observable<Unit> =
        localStorage.delete(movie).toObservable()

    override fun searchMovies(title: String): Observable<MoviesResponse> =
        remoteStorage.searchMovies(title)
            .observeOn(Schedulers.io())
}