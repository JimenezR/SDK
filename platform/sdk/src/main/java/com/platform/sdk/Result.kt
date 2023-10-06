package com.platform.sdk

sealed interface Result<out T, out E> {

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = this is Failure

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]) or
     * `fold(onSuccess = { it }, onFailure = { null })` (see [fold]).
     */
    fun getOrNull(): T? = null

    /**
     * Returns the encapsulated error if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun errorOrNull(): E? = null

    sealed interface Success<T> : Result<T, Nothing> {
        val data: T
        override fun getOrNull(): T? = data
    }

    sealed interface Failure<E> : Result<Nothing, E> {
        val error: E
        override fun errorOrNull(): E? = error
    }

    companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        fun <T> success(value: T): Result<T, Nothing> = SuccessImpl(value)

        /**
         * Returns an instance that encapsulates the given error as failure.
         */
        fun <E> failure(error: E): Result<Nothing, E> = FailureImpl(error)
    }
}

internal data class SuccessImpl<T>(override val data: T) : Result.Success<T>

internal data class FailureImpl<E>(override val error: E) : Result.Failure<E>

/**
 * Calls the specified function [block] and returns its encapsulated result if invocation was successful,
 * catching any [Exception] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
inline fun <R> runCatching(block: () -> R): Result<R, Exception> = try {
    Result.success(block())
} catch (exception: Exception) {
    Result.failure(exception)
}

/**
 * Calls the specified function [block] with `this` value as its receiver and returns its encapsulated result if invocation was successful,
 * catching any [Exception] exception that was thrown from the [block] function execution and encapsulating it as a failure.
 */
inline fun <T, R> T.runCatching(block: T.() -> R): Result<R, Exception> = try {
    Result.success(block())
} catch (e: Exception) {
    Result.failure(e)
}

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or throws the encapsulated [Exception] exception
 * if it is [failure][Result.isFailure].
 *
 * This function is a shorthand for `getOrElse { throw it }` (see [getOrElse]).
 */
fun <T> Result<T, Exception>.getOrThrow(): T = getOrElse { throw it }

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
 * result of [onFailure] function for the encapsulated error if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 *
 * This function is a shorthand for `fold(onSuccess = { it }, onFailure = onFailure)` (see [fold]).
 */
inline fun <R, T : R, E> Result<T, E>.getOrElse(onFailure: (error: E) -> R): R =
    fold(onSuccess = { it }, onFailure = onFailure)

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
 * [defaultValue] if it is [failure][Result.isFailure].
 *
 * This function is a shorthand for `getOrElse { defaultValue }` (see [getOrElse]).
 */
fun <R, T : R, E> Result<T, E>.getOrDefault(defaultValue: R): R = getOrElse { defaultValue }

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [success][Result.isSuccess]
 * or the result of [onFailure] function for the encapsulated error if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onSuccess] or by [onFailure] function.
 */
inline fun <R, T, E> Result<T, E>.fold(
    onSuccess: (value: T) -> R, onFailure: (error: E) -> R
): R {
    @Suppress("UNCHECKED_CAST") return if (isSuccess) onSuccess(getOrNull() as T) else onFailure(
        errorOrNull() as E
    )
}

@PublishedApi
internal inline fun <R, T, E> Result<T, E>.flatMap(
    transform: (value: T) -> Result<R, E>
): Result<R, E> = fold(onSuccess = { transform(it) }, onFailure = {
    @Suppress("UNCHECKED_CAST") this as Result<R, E>
})

@PublishedApi
internal inline fun <R, T, E> Result<T, E>.flatMapErr(
    transform: (error: E) -> Result<T, R>
): Result<T, R> = fold(onSuccess = {
    @Suppress("UNCHECKED_CAST") this as Result<T, R>
}, onFailure = { transform(it) })

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated error if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
inline fun <R, T, E> Result<T, E>.map(transform: (value: T) -> R): Result<R, E> =
    flatMap { Result.success(transform(it)) }

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Exception] exception if it is [failure][Result.isFailure].
 *
 * This function catches any [Throwable] exception thrown by [transform] function and encapsulates it as a failure.
 * See [map] for an alternative that rethrows exceptions from `transform` function.
 */
inline fun <R, T> Result<T, Exception>.mapCatching(
    crossinline transform: (value: T) -> R
): Result<R, Exception> = flatMap { runCatching { transform(it) } }

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated error
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [recoverCatching] for an alternative that encapsulates exceptions.
 */
inline fun <R, T : R, E> Result<T, E>.recover(
    transform: (exception: E) -> R
): Result<R, Nothing> = flatMapErr { Result.success(transform(it)) }

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated [Exception] exception
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 *
 * This function catches any [Exception] exception thrown by [transform] function and encapsulates it as a failure.
 * See [recover] for an alternative that rethrows exceptions.
 */
inline fun <R, T : R, E : Exception> Result<T, E>.recoverCatching(
    crossinline transform: (exception: E) -> R
): Result<R, Exception> = flatMapErr { runCatching { transform(it) } }

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
inline fun <T, E> Result<T, E>.onSuccess(action: (value: T) -> Unit): Result<T, E> {
    @Suppress("UNCHECKED_CAST") if (isSuccess) action(getOrNull() as T)
    return this
}

/**
 * Performs the given [action] on the encapsulated error if this instance represents [failure][Result.isFailure].
 * Returns the original `Result` unchanged.
 */
inline fun <T, E> Result<T, E>.onFailure(action: (error: E) -> Unit): Result<T, E> {
    @Suppress("UNCHECKED_CAST") if (isFailure) action(errorOrNull() as E)
    return this
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated error
 * if this instance represents [failure][Result.isFailure] or the
 * original encapsulated value if it is [success][Result.isSuccess].
 */
@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
inline fun <T> Result<T, Nothing>.unwrap(): T = getOrNull() as T
