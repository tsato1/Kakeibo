//package com.kakeibo.ui;
//
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class FirebaseUserViewModel extends ViewModel {
//
//    /**
//     * LiveData of FirebaseUser. This keeps the UI up-to-date.
//     */
//    public MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();
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
//    public SingleLiveEvent<Void> userChangeEvent = new SingleLiveEvent<>();
//
//    public FirebaseUserViewModel() {
//        updateFirebaseUser();
//    }
//
//    /**
//     * This method should be called when the user completes sign-in or sign-out.
//     */
//    public void updateFirebaseUser() {
//        FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (newUser != null && firebaseUser.getValue() != null &&
//                !newUser.getUid().equals(firebaseUser.getValue().getUid())) {
//            userChangeEvent.call();
//        }
//        firebaseUser.postValue(newUser);
//    }
//
//    /**
//     * Returns true if the user is currently signed in.
//     */
//    public boolean isSignedIn() {
//        return firebaseUser.getValue() != null;
//    }
//}
//
