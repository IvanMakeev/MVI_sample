package com.raywenderlich.wewatch.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raywenderlich.wewatch.common.SingleLiveEvent
import com.raywenderlich.wewatch.domain.Store
import com.raywenderlich.wewatch.domain.action.MovieAction
import com.raywenderlich.wewatch.domain.state.MovieViewState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

/**
 * @author Макеев Иван
 */
open class BaseViewModel(private val store: Store<MovieAction, MovieViewState>) : ViewModel() {

    protected val compositeDisposable = CompositeDisposable()

    /**
     * Нужно для передачи UI Action дальше в store
     */
    private val uiActions = PublishSubject.create<MovieAction>()

    /**
     * Используется SingleLiveEvent потому что MutableLiveData кеширует последнее состояние
     * и получается что два раза рендерятся одни и те же данные
     */
    private val _stateLiveData = SingleLiveEvent<MovieViewState>()

    val stateLiveData: LiveData<MovieViewState> = _stateLiveData

    private var actionsDisposable: Disposable = Disposables.empty()

    /**
     * Инициализируем связывание middleWare со всеми Action, комбинирование Action с ViewState
     * и получаем Disposable для отписки.
     */
    init {
        store.wire()
            .addTo(compositeDisposable)
    }

    /**
     * Подписываемся на получение текущего ViewState из store.
     */
    fun observeViewState() {
        store.observeViewState()
            .subscribe(_stateLiveData::postValue)
            .addTo(compositeDisposable)

    }

    /**
     * Связываем store с Action получаемыми из View.
     */
    fun onAttach() {
        actionsDisposable = store.bind(uiActions.hide())
    }

    /**
     * Отписываемся от получения новых Action.
     * Нужно что бы изменении конфигурации не получать старые Action.
     */
    fun onDetach() {
        actionsDisposable.dispose()
    }

    /**
     * Получаем новые Action из View.
     */
    fun onAction(action: MovieAction) {
        postAction(action)
    }

    /**
     * Передаем новый Action
     */
    private fun postAction(action: MovieAction) {
        uiActions.onNext(action)
    }

    override fun onCleared() {
        if (compositeDisposable.isDisposed.not()) {
            compositeDisposable.dispose()
        }
    }
}