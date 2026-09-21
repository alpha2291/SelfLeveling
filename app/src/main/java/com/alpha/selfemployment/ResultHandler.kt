package com.alpha.selfemployment

sealed class ResultHandler <out T> {

    data class Success<out T>(val data: T) : ResultHandler<T>()

    data class Error(val message: String, val exception: Throwable? = null): ResultHandler<Nothing>()

    object Idle : ResultHandler<Nothing>()

    object Loading : ResultHandler<Nothing>()

}


sealed class UiState<out T> {

    object Idle : UiState<Nothing>()

    object Loading : UiState<Nothing>()

    data class Success<T>(val data: T) : UiState<T>()

    data class Error(val message: String) : UiState<Nothing>()
}