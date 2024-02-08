package com.example.boosthub

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boosthub.data.Repository
import com.example.boosthub.data.datamodel.Chat
import com.example.boosthub.data.datamodel.Event
import com.example.boosthub.data.datamodel.Message
import com.example.boosthub.data.datamodel.User
import com.example.boosthub.data.remote.BoostHubApi
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Firebase Authentication, Firestore and Storage instances.
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    // LiveData for the toast messages.
    private val _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast


    //region FirebaseUserManagement

    // LiveData for the current user.
    private val _user: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val user: LiveData<FirebaseUser?>
        get() = _user

    /*
    The profile document contains a single profile (that of the logged in user).
    A document is like an object.
    */
    lateinit var userRef: DocumentReference

    init {
        setupUserEnv()
    }

    /*
    The setupUserEnv function initializes variables that can be set up when logging in.
    Alternative notation for checking for null values.
    */
    private fun setupUserEnv() {

        _user.value = auth.currentUser

        auth.currentUser?.let { firebaseUser ->

            userRef = firestore.collection("user").document(firebaseUser.uid)
        }
    }

    /*
        The login function attempts to log in the user with the provided credentials.
        If the parameters are correct, the user is logged in.
        If not, the errors are caught.
     */
    fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                setupUserEnv()
            }
        }
            .addOnFailureListener {
                when (it) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        _toast.value = "email or password is not correct"
                    }
                }
            }
    }

    /*
        The signup function attempts to create a new user with the specified credentials.
        If all parameters have been entered correctly, the new user will be created.
        If not, the errors will be caught.
     */
    fun signup(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                setupUserEnv()
                val newUser = User(email)
                userRef.set(newUser)
            }
        }
            .addOnFailureListener {
                when (it) {
                    is FirebaseAuthWeakPasswordException -> {
                        _toast.value =
                            "invalid password." +
                                    "the password should be at least 6 characters long."
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        _toast.value = "invalid E-mail"
                    }
                }
            }
    }

    // The logout function logs the user out.
    fun logout() {
        auth.signOut()
        setupUserEnv()
    }

    /*
       This function is used to clear the LiveData for toast messages,
       by setting the value to an empty string once it is no longer needed.
       This ensures that the toast messages are only displayed once.
    */
    fun emptyLifeData() {
        if (!_toast.value.isNullOrEmpty()) {
            _toast.value = ""
        }
    }

    fun updateUserName(changedUserName: String) {
        userRef.update("userName",changedUserName)
    }

    fun changePassword(newPassword: String,currentPassword: String) {

        val user = auth.currentUser!!

        val email = user.email

        if (!email.isNullOrEmpty()) {

            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthResult ->
                    if (reauthResult.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updatePasswordResult ->
                                if (updatePasswordResult.isSuccessful) {
                                    _toast.value = "password changed successfully"
                                } else {
                                    _toast.value = "password change unsuccessful"
                                }
                            }
                    }
                }
        }
    }

    fun uploadProfileImage(uri: Uri) {

        val imageRef = storage.reference.child("user/${auth.currentUser!!.uid}/image")

        imageRef.putFile(uri).addOnCompleteListener{
            if (it.isSuccessful) {
                imageRef.downloadUrl.addOnCompleteListener { finalImageUrl ->
                    userRef.update("image",finalImageUrl.result.toString())
                }
            }
        }
    }


    //endregion

    //region FirebaseDataManagement

    val eventsRef = firestore.collection("events")

    val chatsRef = firestore.collection("chats")

    fun uploadEvent(event: Event) {
        firestore.collection("events").add(event)
    }

    fun createChat(userId: String) {

        val chat = Chat(
            listOf(
                userId,
                auth.currentUser!!.uid
            )
        )

        firestore.collection("chats").add(chat)
    }

    fun addMessageToChat(message: String,chatId: String) {

        val newMessage = Message(
            content = message,
            senderId = auth.currentUser!!.uid
        )

        firestore.collection("chats").document(chatId).collection("messages").add(newMessage)
    }


    //endregion

    //region api openstreetmap


    private val repository = Repository(BoostHubApi)

    // Create LiveData for the location from the API.
    val location = repository.location

    /*
        The getLocation function retrieves location information from the API.
        If an error occurs, it is caught.
     */
    fun getLocation(searchterm: String) {
        viewModelScope.launch {
            try {
                repository.getLocation(searchterm)
            } catch (e: Exception) {
                Log.e("ViewM", "$e")
            }
        }
    }


    //endregion
}