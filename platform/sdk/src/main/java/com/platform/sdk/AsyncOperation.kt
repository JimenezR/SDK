package com.platform.sdk

sealed class AsyncOperation<out T> {

    /** Returns if this is a loading state. */
    val isLoading by lazy { this is Loading }

    /** Returns if this is a completed success state. */
    val isSuccess by lazy { this is Completed && this.result.isSuccess }

    /** Returns if this is a completed failure state. */
    val isFailure by lazy { this is Completed && this.result.isFailure }

    /** Perform [action] on the result data if any, then return this operation. */
    inline fun onSuccess(action: (data: T) -> Unit): AsyncOperation<T> = apply {
        if (this is Completed) {
            result.onSuccess(action)
        }
    }

    /** Perform [action] on the failure if any, then return this operation. */
    inline fun onFailure(action: (failure: Failure) -> Unit): AsyncOperation<T> = apply {
        if (this is Completed) {
            result.onFailure(action)
        }
    }

    /** Return result data, if any. */
    val data: T?
        get() = when (this) {
            is Loading -> null
            is Completed -> result.getOrNull()
        }

    /** Return error, if any. */
    val error: Failure?
        get() = when (this) {
            is Loading -> null
            is Completed -> result.errorOrNull()
        }
}

/**
 * [Loading] represents the loading/processing status of an operation. This class does not contain any data
 * since the operation is incomplete
 */
data object Loading : AsyncOperation<Nothing>()

/**
 * [Completed] represents the completed status of the operation. It contains a [Result] object that reflects the
 * success or failure of the operation
 *
 * @param T - Type of a successful operation
 * @property result - Contains the success or failure of the operation
 */
class Completed<T>(val result: Result<T, Failure>) : AsyncOperation<T>()

fun <T> loading(): AsyncOperation<T> = Loading
fun <T> success(data: T) = Completed(Result.success(data))
fun <T> failure(failure: Failure) = Completed<T>(Result.failure(failure))

/**
 * Wraps the contents of this result in an AsyncOperation.Complete performing the passed [transform] on the success: T
 * payload.
 *
 * @param transform the operation performed to convert from <T> to <K>. If no transformation is required, use the other
 * [toAsyncOperation] function that will perform an identity transformation ( { it } )
 */
inline fun <T, K, E : Failure> Result<T, E>.toAsyncOperation(transform: (T) -> K): Completed<K> =
    fold(onSuccess = { success(transform(it)) }, onFailure = { failure(it) })

/** Wraps the contents of this result in an AsyncOperation.Complete without any transformation */
fun <T, E : Failure> Result<T, E>.toAsyncOperation(): Completed<T> = toAsyncOperation { it }
