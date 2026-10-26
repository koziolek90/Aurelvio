package com.kris.aurelvio.core.common.result

/**
 * A discriminated union representing either a success result [Outcome.Success]
 * with data of type [T], or a failure [Outcome.Failure] with an error of type [E].
 */
sealed interface Outcome<out T, out E> {

    data class Success<out T>(val data: T) : Outcome<T, Nothing>

    data class Failure<out E>(val error: E) : Outcome<Nothing, E>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    fun errorOrNull(): E? = when (this) {
        is Success -> null
        is Failure -> error
    }
}

inline fun <T, E, R> Outcome<T, E>.fold(
    onSuccess: (T) -> R,
    onFailure: (E) -> R
): R = when (this) {
    is Outcome.Success -> onSuccess(data)
    is Outcome.Failure -> onFailure(error)
}

inline fun <T, E, R> Outcome<T, E>.map(transform: (T) -> R): Outcome<R, E> = when (this) {
    is Outcome.Success -> Outcome.Success(transform(data))
    is Outcome.Failure -> this
}
