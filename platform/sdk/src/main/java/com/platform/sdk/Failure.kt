package com.platform.sdk

/**
 * [Failure] is a base type representing failed operations. Below are common types of failure for
 * general purpose use, but domain specific [Failure] subtypes can be created where it can add
 * value to an operation and its consumers
 */
interface Failure {
    /**
     * Human readable message that describes what the failure is. Equivalent of [Throwable.message]
     */
    val message: String?
        get() = null
}

/**
 * [NetworkConnectionFailure] used to represent network timeouts or other failure to use the network
 *
 * @param errorData any error data in the response
 */
open class NetworkConnectionFailure<ErrorData>(
    val errorData: ErrorData? = null
) : Failure

/**
 * [ServiceFailure] represents an error on the service-side of a network request. Typical usage is whenever an HTTP 4XX
 * or 5XX status code is encountered
 *
 * @param errorData any error data in the response
 */
open class ServiceFailure<ErrorData>(
    val errorData: ErrorData? = null
) : Failure

/**
 * [ExceptionFailure] wraps an exception that resulted in a [Failure]
 *
 * @param throwable - The exception
 */
open class ExceptionFailure(val throwable: Throwable) : Failure {
    override val message: String?
        get() = "ExceptionFailure: ${toString()}"
    override fun toString(): String {
        return throwable.toString()
    }
}

/**
 * [FunctionalityDisabledFailure] returned when trying to access a screen/feature that has been disabled by CCM.
 *
 * Identify the call site with the [tag] and provide a [description] of the in
 * which this may have been called for debugging purposes. The [ccmKey] will help
 * us identify immediately the controls by which we can enable blocked features.
 */
class FunctionalityDisabledFailure(
    private val tag: String,
    private val description: String,
    private val ccmKey: String,
) : Failure {
    override val message by lazy {
        "Functionality Disabled, tag: $tag, description: $description, ccmKey: $ccmKey"
    }
}
