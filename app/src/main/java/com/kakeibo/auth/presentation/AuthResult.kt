package com.kakeibo.auth.presentation

sealed class AuthResult<T>(val data: T? = null) {
    class BadRequest<T>: AuthResult<T>()
    class ConnectionError<T>: AuthResult<T>() // cannot connect to server (server might be down)
    class Authorized<T>(data: T? = null): AuthResult<T>(data)
    class Unauthorized<T>: AuthResult<T>()
    class NoContent<T>: AuthResult<T>() // item was deleted
    class UserAlreadyExists<T>: AuthResult<T>() // user was found in db of auth server
    class UserNotInDatabase<T>: AuthResult<T>() // user was not found in db of auth server
    class InvalidEmailOrPassword<T>: AuthResult<T>() // email or password is invalid
    class NotOnline<T>: AuthResult<T>() // check wifi is turned on, airplane mode is off, etc.
    class DifferentDevice<T>: AuthResult<T>() // detect the login is done via different device from the last time
    class Canceled<T>: AuthResult<T>() // coroutine canceled
    class UnknownError<T>: AuthResult<T>()
}