package com.tasomaniac.openwith.rx

import io.reactivex.rxjava3.core.CompletableTransformer
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.core.MaybeTransformer
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.disposables.Disposable

class SchedulingStrategy(private val executor: Scheduler, private val notifier: Scheduler) {

    fun <T : Any> forObservable() = ObservableTransformer<T, T> { observable ->
        observable
            .subscribeOn(executor)
            .observeOn(notifier)
    }

    fun <T : Any> forFlowable() = FlowableTransformer<T, T> { flowable ->
        flowable
            .subscribeOn(executor)
            .observeOn(notifier)
    }

    fun <T : Any> forMaybe() = MaybeTransformer<T, T> { maybe ->
        maybe
            .subscribeOn(executor)
            .observeOn(notifier)
    }

    fun forCompletable() = CompletableTransformer { completable ->
        completable
            .subscribeOn(executor)
            .observeOn(notifier)
    }

    fun <T : Any> forSingle() = SingleTransformer<T, T> { single ->
        single
            .subscribeOn(executor)
            .observeOn(notifier)
    }

    fun runOnNotifier(runnable: Runnable): Disposable {
        return runOnWorker(runnable, notifier.createWorker())
    }

    fun runOnExecutor(runnable: Runnable): Disposable {
        return runOnWorker(runnable, executor.createWorker())
    }

    private fun runOnWorker(runnable: Runnable, worker: Scheduler.Worker): Disposable {
        return worker.schedule {
            try {
                runnable.run()
            } finally {
                worker.dispose()
            }
        }
    }
}
