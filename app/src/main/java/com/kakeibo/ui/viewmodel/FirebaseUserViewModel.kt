//package com.kakeibo.ui.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.kakeibo.ui.SingleLiveEvent
//
//class FirebaseUserViewModel : ViewModel() {
//    /**
//     * LiveData of FirebaseUser. This keeps the UI up-to-date.
//     */
//    val firebaseUser = MutableLiveData<FirebaseUser?>()
//
//    /**
//     * When the user changes, the app needs to notify the server.
//     * A normal LiveData is called more often in order to keep the UI up-to-date.
//     * This event is a more efficient indicator that the user might have changed.
//     * This event could be fired more often than needed because the ViewModel
//     * can be destroyed and recreated even if the user does not change.
//     * To improve this, we could store authentication information to disk in order
//     * to make sure we only call the server when the user actually changes,
//     * however this implementation seems to be efficient enough for this sample.
//     */
//    val userChangeEvent = SingleLiveEvent<Void>()
//
//    init {
//        updateFirebaseUser()
//    }
//    /**
//     * This method should be called when the user completes sign-in or sign-out.
//     */
//    fun updateFirebaseUser() {
//        val newUser = FirebaseAuth.getInstance().currentUser
//        if (newUser?.uid != firebaseUser.value?.uid) {
//            userChangeEvent.call()
//        }
//        firebaseUser.postValue(newUser)
//    }
//
//    /**
//     * Returns true if the user is currently signed in.
//     */
//    fun isSignedIn() = firebaseUser.value != null
//}