package com.raywenderlich.wewatch.domain

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * @author Макеев Иван
 */

interface ViewState

interface Action

abstract class MiddleWare<A : Action, V : ViewState> {
    abstract fun bind(action: Observable<A>): Observable<A>
}


interface Store<A : Action, V : ViewState> {

    /**
     * bind all actions with middleware
     */
    fun wire(): Disposable

    /**
     * bind only ui actions to all actions
     */
    fun bind(uiAction: Observable<A>): Disposable

    fun observeViewState(): Observable<V>
}


interface Reducer<A : Action, V : ViewState> {
    fun reduce(viewState: V, action: A): V
}