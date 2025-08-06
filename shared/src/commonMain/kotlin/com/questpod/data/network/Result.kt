package com.questpod.data.network

/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with an arbitrary [Exception].
 */
sealed class Result<out T> {
    /**
     * Represents successful operation with the given [data].
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Represents failed operation with the given [exception].
     */
    data class Error(val exception: Exception) : Result<Nothing>()
    
    /**
     * Returns true if this is a Success, false otherwise.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns true if this is an Error, false otherwise.
     */
    val isError: Boolean get() = this is Error
    
    /**
     * Returns the encapsulated data if this instance represents [Success] or throws the encapsulated [Exception] if it is [Error].
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
    }
    
    /**
     * Returns the encapsulated data if this instance represents [Success] or null if it is [Error].
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    /**
     * Returns the encapsulated data if this instance represents [Success] or the [defaultValue] if it is [Error].
     */
    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> defaultValue
    }
    
    /**
     * Maps the success value using the given [transform] function.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
    }
    
    /**
     * Performs the given [action] on the encapsulated data if this instance represents [Success].
     * Returns the original Result unchanged.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    /**
     * Performs the given [action] on the encapsulated [Exception] if this instance represents [Error].
     * Returns the original Result unchanged.
     */
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}
