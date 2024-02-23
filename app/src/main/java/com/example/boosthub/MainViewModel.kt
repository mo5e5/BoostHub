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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Initialize Firebase Authentication, Firestore and Storage instances.
     */
    val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage


    /**
     * LiveData for the toast messages.
     */
    private val _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast


    //region FirebaseUserManagement (commented)

    /**
     * LiveData for the current user.
     */
    private val _user: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val user: LiveData<FirebaseUser?>
        get() = _user

    /**
     * The profile document contains a single profile (that of the logged in user).
     * A document is like an object.
     */
    lateinit var currentUserRef: DocumentReference

    init {
        setupUserEnv()
    }

    /**
     * The setupUserEnv function initializes variables that can be set up when logging in.
     * Alternative notation for checking for null values.
     */
    private fun setupUserEnv() {
        _user.value = auth.currentUser
        auth.currentUser?.let { firebaseUser ->
            currentUserRef = firestore.collection("user").document(firebaseUser.uid)
        }
    }

    /**
     * The login function attempts to log in the user with the provided credentials.
     * If the parameters are correct, the user is logged in.
     * If not, the errors are caught.
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

    /**
     * The signup function attempts to create a new user with the specified credentials.
     * If all parameters have been entered correctly, the new user will be created.
     * If not, the errors will be caught.
     */
    fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                setupUserEnv()
                val newUser = User(email)
                currentUserRef.set(newUser)
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

    /**
     * The logout function logs the user out.
     */
    fun logout() {
        auth.signOut()
        setupUserEnv()
    }

    /**
     * This function is used to clear the LiveData for toast messages,
     * by setting the value to an empty string once it is no longer needed.
     * This ensures that the toast messages are only displayed once.
     */
    fun emptyLifeData() {
        if (!_toast.value.isNullOrEmpty()) {
            _toast.value = ""
        }
    }

    /**
     * Update the username in the user's Firestore document
     */
    fun updateUserData(changedUserName: String, addedCurrentCars: String) {
        currentUserRef.update("userName", changedUserName)
        currentUserRef.update("currentCars", addedCurrentCars)
    }

    /**
     * This function changes the password of the current user.
     * They have to re-authenticate themselves with their current password and can then create a new password.
     * The input is checked and a toast is executed.
     */
    fun changePassword(newPassword: String, currentPassword: String) {
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

    /**
     * This function allows you to upload a profile image.
     * A storage location is created in Fierebase Storage for the user's professional image.
     * If the upload was successful, the download URL of the image will be retrieved.
     */
    fun uploadProfileImage(uri: Uri) {
        val imageRef = storage.reference.child("user/${auth.currentUser!!.uid}/image")
        imageRef.putFile(uri).addOnCompleteListener {
            if (it.isSuccessful) {
                imageRef.downloadUrl.addOnCompleteListener { finalImageUrl ->
                    currentUserRef.update("image", finalImageUrl.result.toString())
                }
            }
        }
    }


    //endregion

    //region FirebaseDataManagement (not all commented)

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>>
        get() = _userList

    /**
     * LiveData for Event ID.
     */
    private val _eventId = MutableLiveData<String>()
    val eventId: LiveData<String>
        get() = _eventId

    private val _currentEvent = MutableLiveData<Event>()
    val currentEvent: LiveData<Event>
        get() = _currentEvent

    /**
     * LiveData for the event image URL.
     */
    private val _eventImageUrl = MutableLiveData<String>()
    val eventImageUrl: LiveData<String>
        get() = _eventImageUrl

    /**
     * Reference to the Firestore collection "events".
     */
    val eventsRef = firestore.collection("events")

    /**
     * Reference to the Firestore collection "chats".
     */
    val chatsRef = firestore.collection("chats")

    /**
     * Reference to the Firestore collection "user"
     */
    val userRef = firestore.collection("user")

    //region FirebaseChatManagement

    /**
     * This feature creates a chat document.
     * A chat object is created with the user IDs.
     * The chat document will be added to the Firestore chats collection.
     */
    private fun createChatById(userId: String) {
        val chat = Chat(
            listOf(
                userId,
                auth.currentUser!!.uid
            )
        )
        firestore.collection("chats").add(chat)
    }

    fun createChatByEmail(email: String) {
        userRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    val userId = it.documents[0].id
                    Log.d("testing", userId)
                    createChatById(userId)
                }
            }
    }

    fun addUserById(userId: String) {
        val userDoc = userRef.document(userId)
        userDoc.get().addOnSuccessListener {
            it.toObject<User>()!!
        }
    }

    /**
     * This function add a message to a chat document.
     * A new message will be created with content and sender ID.
     * The message will be added to the "messages" collection of the chat document.
     */
    fun addMessageToChat(message: String, chatId: String) {
        val newMessage = Message(
            content = message,
            senderId = auth.currentUser!!.uid
        )
        firestore.collection("chats").document(chatId).collection("messages").add(newMessage)
    }

    fun getMessageRef(chatId: String): CollectionReference {
        return firestore.collection("chats").document(chatId).collection("messages")
    }

    //endregion

    //region FirebaseEventManagement

    private fun uploadEventId(eventId: String) {
        firestore.collection("events").document(eventId).update("eventId", eventId)
    }

    /**
     * This function is responsible for uploading an event document and its image to Firebase Storage.
     * If the event document is successfully added, the event ID will be retrieved and placed in a variable.
     * The event image will be uploaded and its URL will be stored in Firebase Storage.
     */
    fun uploadEvent(event: Event, eventImage: Uri) {
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                val eventId = documentReference.id
                _eventId.value = eventId
                uploadEventId(eventId)
                uploadEventImage(eventImage, eventId)
            }
    }

    fun updateEvent(event: Event, eventImage: Uri, eventId: String) {
        firestore.collection("events").document(eventId).set(event)
            .addOnSuccessListener {
                uploadEventImage(eventImage, eventId)
            }
    }

    fun clearEvent(){
        _currentEvent.value = Event(
            image = "",
            whatsUp = "",
            location = "",
            date = "",
            whosThere = "",
            whatElse = "",
            restrictions = "",
            creatorId = "",
            eventId = "",
        )
    }

    fun deleteEvent(eventId: String) {
        firestore.collection("events").document(eventId).delete()
    }

    /**
     * This feature is for uploading an image to Firebase Storage and updating the image URL in the event document.
     * This is the reference to the location of the image in Firebase Storage.
     * File name will be created for the image.
     * Reference to the file in Firebase Storage is created.
     * The image will be uploaded to Firebase Storage.
     * If the image is successfully uploaded, its download URL will be retrieved and set.
     * The image URL is stored as a string and updated in the LiveData object.
     * The image URL will be updated in the associated event document in Firestore.
     */
    private fun uploadEventImage(uri: Uri, eventId: String) {
        val imageRef = storage.reference.child("event/")
        val fileName = "image_${eventId}"
        val fileRef = imageRef.child(fileName)
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                _eventImageUrl.value = imageUrl
                eventsRef.document(eventId).update("image", imageUrl)
            }
        }
    }

    fun getEventById(eventId: String) {
        Log.d("eventID", eventId)
        if(eventId != "0"){
            eventsRef.document(eventId).get().addOnSuccessListener {
                val event = it.toObject<Event>()!!
                _currentEvent.value = event
            }
        }
    }

    //endregion

    //endregion

    //region api openstreetmap (commented)

    private val repository = Repository(BoostHubApi)

    /**
     * Create LiveData for the location from the API.
     */
    val location = repository.location

    /**
     * The getLocation function retrieves location information from the API.
     * If an error occurs, it is caught.
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