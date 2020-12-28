package com.raywenderlich.wewatch.view.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.wewatch.domain.Store
import com.raywenderlich.wewatch.domain.action.MovieAction
import com.raywenderlich.wewatch.domain.state.MovieViewState
import com.raywenderlich.wewatch.view.viewmodel.BaseViewModel

/**
 * @author Макеев Иван
 */
class BaseViewModelFactory(private val store: Store<MovieAction, MovieViewState>) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaseViewModel::class.java)) {
            return BaseViewModel(store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}