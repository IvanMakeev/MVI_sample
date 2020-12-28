package com.raywenderlich.wewatch.common

import android.content.Context
import com.raywenderlich.wewatch.R

class ResourceProvider(private val applicationContext: Context) : IResourceProvider {

    override fun provideErrorMessage(): String = applicationContext.getString(R.string.error_message)

    override fun provideDeleteMessage(movieName: String): String = applicationContext.getString(R.string.deleted_movie_message, movieName)

    override fun provideDidntFindMoviesMessage(): String = applicationContext.getString(R.string.didn_find_movies_message)
}