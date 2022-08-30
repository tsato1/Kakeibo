package com.kakeibo.auth.data

import android.app.Application
import android.content.Context
import com.kakeibo.Constants
import com.kakeibo.auth.presentation.AuthResult
import com.kakeibo.core.data.remote.AuthApi
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.data.remote.*
import com.kakeibo.core.data.remote.requests.AuthRequest
import com.kakeibo.core.data.remote.requests.Device
import com.kakeibo.auth.domain.repositories.AuthRepository
import com.kakeibo.core.data.remote.requests.TokenRequest
import com.kakeibo.core.util.NetworkWatcher
import com.kakeibo.util.getIpAddress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: AuthApi,
    private val prefs: AppPreferences
) : AuthRepository {

    override suspend fun register(
        email: String, password: String
    ): AuthResult<Unit> = withContext(Dispatchers.IO) {
        if (!NetworkWatcher.getInstance(context.applicationContext as Application).isOnline) {
            return@withContext AuthResult.NotOnline()
        }

        try {
            api.register(
                authRequest = AuthRequest(
                    email,
                    password,
                    Device(
                        ip = getIpAddress(),
                        manufacturer = android.os.Build.MANUFACTURER,
                        model = android.os.Build.MODEL
                    )
                )
            )

            login(email, password)
        }
        catch (e: HttpException) {
            when (e.code()) {
                400 -> AuthResult.BadRequest()
                401 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: ServiceException) {
            when (e.code()) {
                491 -> AuthResult.UserAlreadyExists()
                492 -> AuthResult.UserNotInDatabase()
                493 -> AuthResult.InvalidEmailOrPassword()
                494 -> AuthResult.DifferentDevice()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: IOException) { AuthResult.ConnectionError() }
        catch (e: CancellationException) { AuthResult.Canceled() }
        catch (e: Exception) { AuthResult.UnknownError() }
    }

    override suspend fun login(
        email: String, password: String
    ): AuthResult<Unit> = withContext(Dispatchers.IO) {
        if (!NetworkWatcher.getInstance(context.applicationContext as Application).isOnline) {
            return@withContext AuthResult.NotOnline()
        }

        try {
            val response = api.login(
                authRequest = AuthRequest(
                    email,
                    password,
                    Device(
                        ip = getIpAddress(),
                        manufacturer = android.os.Build.MANUFACTURER,
                        model = android.os.Build.MODEL
                    )
                )
            )

            prefs.set(Constants.PREFS_KEY_JWT_ACCESS_TOKEN, response.accessToken)
            prefs.set(Constants.PREFS_KEY_JWT_REFRESH_TOKEN, response.refreshToken)
            AuthResult.Authorized()
        }
        catch (e: HttpException) {
            when (e.code()) {
                400 -> AuthResult.BadRequest()
                401 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: ServiceException) {
            when (e.code()) {
                491 -> AuthResult.UserAlreadyExists()
                492 -> AuthResult.UserNotInDatabase()
                493 -> AuthResult.InvalidEmailOrPassword()
                494 -> AuthResult.DifferentDevice()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: IOException) { AuthResult.ConnectionError() }
        catch (e: CancellationException) { AuthResult.Canceled() }
        catch (e: Exception) { AuthResult.UnknownError() }
    }

    override suspend fun refreshAccessToken(): AuthResult<Unit> = withContext(Dispatchers.IO) {
        if (!NetworkWatcher.getInstance(context.applicationContext as Application).isOnline) {
            return@withContext AuthResult.NotOnline()
        }

        val refreshToken = prefs.getRefreshToken()
        if (refreshToken == Constants.NO_JWT_TOKEN) {
            return@withContext AuthResult.Unauthorized()
        }

        try {
            val response = api.refreshAccessToken(
                refreshToken = refreshToken,
                tokenRequest = TokenRequest(
                    device = Device(
                        ip = getIpAddress(),
                        manufacturer = android.os.Build.MANUFACTURER,
                        model = android.os.Build.MODEL
                    )
                )
            )

            prefs.set(Constants.PREFS_KEY_JWT_ACCESS_TOKEN, response.accessToken)
            prefs.set(Constants.PREFS_KEY_JWT_REFRESH_TOKEN, response.refreshToken)
            AuthResult.Authorized()
        }
        catch (e: HttpException) {
            when (e.code()) {
                400 -> AuthResult.BadRequest()
                401 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: ServiceException) {
            when (e.code()) {
                491 -> AuthResult.UserAlreadyExists()
                492 -> AuthResult.UserNotInDatabase()
                493 -> AuthResult.InvalidEmailOrPassword()
                494 -> AuthResult.DifferentDevice()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: IOException) { AuthResult.ConnectionError() }
        catch (e: CancellationException) { AuthResult.Canceled() }
        catch (e: Exception) { AuthResult.UnknownError() }
    }

    override suspend fun logout(): AuthResult<Unit> = withContext(Dispatchers.IO) {
        if (!NetworkWatcher.getInstance(context.applicationContext as Application).isOnline) {
            return@withContext AuthResult.NotOnline()
        }

        val refreshToken = prefs.getRefreshToken()
        if (refreshToken == Constants.NO_JWT_TOKEN) {
            return@withContext AuthResult.Unauthorized()
        }

        try {
            api.logout(
                refreshToken = refreshToken,
                tokenRequest = TokenRequest(
                    device = Device(
                        ip = getIpAddress(),
                        manufacturer = android.os.Build.MANUFACTURER,
                        model = android.os.Build.MODEL
                    )
                )
            )
            prefs.set(Constants.PREFS_KEY_JWT_ACCESS_TOKEN, Constants.NO_JWT_TOKEN)
            prefs.set(Constants.PREFS_KEY_JWT_REFRESH_TOKEN, Constants.NO_JWT_TOKEN)
            AuthResult.NoContent()
        }
        catch (e: HttpException) {
            when (e.code()) {
                400 -> AuthResult.BadRequest()
                401 -> AuthResult.Unauthorized()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: ServiceException) {
            when (e.code()) {
                491 -> AuthResult.UserAlreadyExists()
                492 -> AuthResult.UserNotInDatabase()
                493 -> AuthResult.InvalidEmailOrPassword()
                494 -> AuthResult.DifferentDevice()
                else -> AuthResult.UnknownError()
            }
        }
        catch (e: IOException) { AuthResult.ConnectionError() }
        catch (e: CancellationException) { AuthResult.Canceled() }
        catch (e: Exception) { AuthResult.UnknownError() }
    }

}