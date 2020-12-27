package com.raywenderlich.wewatch.view.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * @author Макеев Иван
 */
open class BaseViewModel : ViewModel() {

    protected val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        if (compositeDisposable.isDisposed.not()) {
            compositeDisposable.dispose()
        }
    }
}