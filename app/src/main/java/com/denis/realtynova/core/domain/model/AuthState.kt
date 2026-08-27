package com.denis.realtynova.core.domain.model

sealed interface AuthState {
    data object Initial : AuthState
    data class Authenticated(val user: User) : AuthState
    data object Unauthenticated : AuthState
}
