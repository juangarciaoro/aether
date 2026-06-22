package com.aether.core.common.result

sealed interface AetherResult<out T> {
    data class Success<T>(val data: T) : AetherResult<T>
    data class Error(val exception: Throwable, val message: String? = null) : AetherResult<Nothing>
    data object Loading : AetherResult<Nothing>
}

inline fun <T> AetherResult<T>.onSuccess(action: (T) -> Unit): AetherResult<T> {
    if (this is AetherResult.Success) action(data)
    return this
}

inline fun <T> AetherResult<T>.onError(action: (Throwable) -> Unit): AetherResult<T> {
    if (this is AetherResult.Error) action(exception)
    return this
}

fun <T> AetherResult<T>.getOrNull(): T? = (this as? AetherResult.Success)?.data
