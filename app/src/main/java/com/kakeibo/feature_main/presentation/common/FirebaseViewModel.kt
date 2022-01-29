package com.kakeibo.feature_main.presentation.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakeibo.core.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(

): ViewModel() {
    /**
     * LiveData of FirebaseUser. This keeps the UI up-to-date.
     */
    private val _firebaseUser = mutableStateOf<FirebaseUser?>(null)
    val firebaseUser: State<FirebaseUser?> = _firebaseUser

    /**
     * When the user changes, the app needs to notify the server.
     * A normal LiveData is called more often in order to keep the UI up-to-date.
     * This event is a more efficient indicator that the user might have changed.
     * This event could be fired more often than needed because the ViewModel
     * can be destroyed and recreated even if the user does not change.
     * To improve this, we could store authentication information to disk in order
     * to make sure we only call the server when the user actually changes,
     * however this implementation seems to be efficient enough for this sample.
     */
    private val userChangeEvent = SingleLiveEvent<Void>()

    init {
        updateFirebaseUser()
    }

    fun updateFirebaseUser() {
        val newUser = FirebaseAuth.getInstance().currentUser
        if (newUser?.uid != firebaseUser.value?.uid) {
            userChangeEvent.call()
        }
        _firebaseUser.value = newUser
    }

    fun isSignedIn() = firebaseUser.value != null
}