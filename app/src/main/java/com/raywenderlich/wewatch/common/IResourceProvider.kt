package com.raywenderlich.wewatch.common

interface IResourceProvider {
    fun provideErrorMessage(): String
    fun provideDeleteMessage(movieName: String): String
    fun provideDidntFindMoviesMessage(): String
}
