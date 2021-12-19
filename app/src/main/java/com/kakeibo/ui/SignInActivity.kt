//package com.kakeibo.ui
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import com.kakeibo.R
//import com.kakeibo.databinding.ActivitySigninBinding
//import kotlinx.coroutines.*
//import kotlinx.coroutines.tasks.await
//
//class SignInActivity : AppCompatActivity() {
//
//    companion object {
//        const val TAG: String = "SignInActivity"
//
//        private const val REQUEST_CODE_SIGN_IN = 0
//    }
//
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    private lateinit var binding: ActivitySigninBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySigninBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnGoogleSignIn.setOnClickListener {
//            signInGoogle()
//        }
//        binding.btnSignIn.setOnClickListener {
//            signInUser()
//        }
//        binding.btnRegister.setOnClickListener {
//            registerUser()
//        }
//        binding.txvRegister.setOnClickListener {
//            binding.lnlRegister.visibility = View.VISIBLE
//        }
//
//        firebaseAuth = FirebaseAuth.getInstance()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        checkSignInState()
//    }
//
//    private fun signInGoogle() {
//        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//        val signInClient = GoogleSignIn.getClient(this, options)
//        signInClient.signInIntent.also {
//            startActivityForResult(it, REQUEST_CODE_SIGN_IN)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CODE_SIGN_IN) {
//            val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(data).result
//
//            googleAccount?.let {
//                val credentials = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
//
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        firebaseAuth.signInWithCredential(credentials).await()
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(this@SignInActivity, "Sign-In Successful", Toast.LENGTH_LONG).show()
//                        }
//                    } catch (e: java.lang.Exception) {
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun signInUser() {
//        val email = binding.edtSignInEmail.text.toString()
//        val password = binding.edtSignInPassword.text.toString()
//
//        if (email.isEmpty()) {
//            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show()
//            return
//        }
//        if (password.isEmpty()) {
//            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
//                    withContext(Dispatchers.Main) {
//                        checkSignInState()
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun registerUser() {
//        val email = binding.edtRegisterEmail.text.toString()
//        val password = binding.edtRegisterPassword.text.toString()
//
//        if (email.isEmpty()) {
//            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show()
//            return
//        }
//        if (password.isEmpty()) {
//            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//                    withContext(Dispatchers.Main) {
//                        checkSignInState()
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun checkSignInState() {
//        if (firebaseAuth.currentUser == null) {
//            Log.d(TAG, "You are not signed in.")
//        }
//        else {
//            Log.d(TAG, "You are signed in.")
//        }
//    }
//}