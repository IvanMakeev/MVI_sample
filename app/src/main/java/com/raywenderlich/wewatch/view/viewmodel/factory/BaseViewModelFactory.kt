package com.raywenderlich.wewatch.view.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.wewatch.domain.interactor.Interactor
import com.raywenderlich.wewatch.view.viewmodel.AddViewModel
import com.raywenderlich.wewatch.view.viewmodel.MainViewModel
import com.raywenderlich.wewatch.view.viewmodel.SearchViewModel

/**
 * @author Макеев Иван
 */
class BaseViewModelFactory(private val movieInteractor: Interactor) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(movieInteractor) as T
        }
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(movieInteractor) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(movieInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}