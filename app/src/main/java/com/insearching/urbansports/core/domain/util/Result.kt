package com.insearching.urbansports.core.domain.util

typealias DomainError = Error

sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: DomainError>(val error: E): Result<Nothing, E>
}

inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

fun <T, E: Error> Result<T, E>.getOrDefault(defaultValue: T): T {
    return when (this) {
        is Result.Error -> defaultValue
        is Result.Success -> data
    }
}

fun <T, E: Error> Result<T, E>.errorOrNull(): E? {
    return when (this) {
        is Result.Error -> error
        is Result.Success -> null
    }
}



fun <T, E: Error> Result<T, E>.isSucceeded(): Boolean {
    return this is Result.Success
}


inline fun <T, E: Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>