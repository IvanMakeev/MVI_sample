package com.raywenderlich.wewatch.domain.store

import com.raywenderlich.wewatch.domain.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * @author Макеев Иван
 */
class DefaultStore<A : Action, V : ViewState>(
    private val reducer: Reducer<A, V>,
    private val middleWare: MiddleWare<A, V>,
    initialState: V
) : Store<A, V> {

    private val allActions: PublishSubject<A> = PublishSubject.create()
    private val states: BehaviorSubject<V> = BehaviorSubject.createDefault(initialState)

    /**
     Связываем все Action с middleWare для их обработки в middleWare.
     Подписываемся на Action, которые вернутся из middleWare.
     Комбинируем последний Action с последним ViewState в редюсере, после подписываемся и передаем
     новые стейты в states.
     */
    override fun wire(): Disposable {
        val compositeDisposable = CompositeDisposable()

        middleWare.bind(allActions)
            .subscribe(allActions::onNext)
            .addTo(compositeDisposable)

        allActions
            .withLatestFrom(states) { action, viewState -> reducer.reduce(viewState, action) }
            .subscribe(states::onNext)
            .addTo(compositeDisposable)

        return compositeDisposable
    }

    /**
     * Связываем uiAction передаваемые из ViewModel со всеми allActions.
     */
    override fun bind(uiAction: Observable<A>): Disposable =
        uiAction.subscribe(allActions::onNext)

    /**
     * Получаем Observable который эмитет все новые ViewState из редюсера.
     */
    override fun observeViewState(): Observable<V> = states.hide()

}