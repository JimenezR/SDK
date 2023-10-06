package com.platform.sdk.mvvm

import com.platform.sdk.AsyncOperation
import com.platform.sdk.Completed
import com.platform.sdk.Failure
import com.platform.sdk.Result
import com.platform.sdk.loading
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * [FlowUseCase] is a UseCase type that returns a cold [Flow] with [AsyncOperation] so the status of the ongoing UseCase operation can
 * continuously observed rather than waiting for the final [Completed]. This relieves the ViewModel layer from having to notify
 * View layer of a loading state when kicking off the [FlowUseCase] since the ViewModel should be passing the [Flow] from
 * [FlowUseCase] to the View layer directly.
 *
 * This type excels for simple data fetching scenarios (single repository query -> data transformation -> display data)
 *
 * @param T - Return type when [FlowUseCase] successfully completes
 */
abstract class FlowUseCase<T>(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    /**
     * Public method for executing the business logic of this [FlowUseCase]. Returns a [Flow] that will emit [Loading]
     * and then the result of executing the actual business logic in [FlowUseCase.executeInternal]
     *
     * @return a flow that will emit [Loading] and [Completed]
     */
    fun execute(): Flow<AsyncOperation<T>> = flow {
        emit(loading())
        emit(withContext(ioDispatcher) { executeInternal() })
    }

    /**
     * Protected method for encapsulating the actual business logic of the [FlowUseCase] subtype
     *
     * @return the result of the logic (success or failure)
     */
    protected abstract suspend fun executeInternal(): Completed<T>
}

/**
 * [ResultUseCase] is an alternative UseCase interface that doesn't expose an observable. This type can be used in scenarios
 * where the View layer doesn't need to know the pending status of the UseCase's operations. This type can
 * also be used in scenarios where the View _does_ need to know the pending status, however it would be up to the ViewModel
 * for reporting the pending status to the UseCase prior to executing it
 *
 * @param T - Return type when [ResultUseCase] successfully completes
 */
interface ResultUseCase<T> {
    /** Executes the business logic of this UseCase */
    suspend fun execute(): UseCaseResult<T>
}

/** [UseCaseResult] is a convenience typealias for declaring the right side of a [Result] to be of type [Failure] */
typealias UseCaseResult<T> = Result<T, Failure>