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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Initialize Firebase Authentication, Firestore and Storage instances.
    val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    // LiveData for the toast messages.
    private val _toast: MutableLiveData<String> = MutableLiveData()
    val toast: LiveData<String>
        get() = _toast


    //region FirebaseUserManagement

    /**
     * LiveData for the current user.
     */
    private val _user: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val user: LiveData<FirebaseUser?>
        get() = _user

    /**
     * The profile document contains a single profile (that of the logged in user).
     */
    lateinit var currentUserRef: DocumentReference

    init {
        setupUserEnv()
    }

    /**
     * The setupUserEnv function initializes variables that can be set up when logging in.
     */
    private fun setupUserEnv() {
        _user.value = auth.currentUser
        auth.currentUser?.let { firebaseUser ->
            currentUserRef = firestore.collection("user").document(firebaseUser.uid)
        }
    }

    /**
     * Attempts to log in the user with the provided credentials.
     * If the credentials are correct, the user is logged in and the environment is set up.
     * If not, errors will be caught and an appropriate toast message will be displayed.
     *
     * @param email The user's email address.
     * @param password The user's password.
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
     * Attempts to create a new user with the provided credentials.
     * If all parameters are entered correctly, the new user is created and the environment is set up.
     * If not, errors are caught and corresponding toast messages are displayed.
     *
     * @param email The new user's email address.
     * @param password The new user's password.
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

    // The logout function logs the user out.
    fun logout() {
        auth.signOut()
        setupUserEnv()
    }

    /**
     * Empties the LiveData for Toast messages by setting the value to an empty string,
     * as soon as it is no longer needed. This ensures that the toast messages are only displayed once.
     */
    fun emptyLifeData() {
        if (!_toast.value.isNullOrEmpty()) {
            _toast.value = ""
        }
    }

    /**
     * Updates user data in the user's Firestore document.
     *
     * @param changedUserName The changed user name.
     * @param addedCurrentCars The current vehicles added.
     */
    fun updateUserData(changedUserName: String, addedCurrentCars: String) {
        currentUserRef.update("userName", changedUserName)
        currentUserRef.update("currentCars", addedCurrentCars)
    }

    /**
     * Changes the current user's password.
     * The user must re-authenticate with their current password and can then create a new password.
     * The input is verified and a toast is made.
     *
     * @param newPassword The new password to set.
     * @param currentPassword The user's current password.
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
     * Uploads a profile picture.
     * A location is created in Firebase Storage for the user's profile picture.
     * If the upload is successful, the download URL of the image will be retrieved.
     * The image URL is saved as a string and updated in the LiveData object.
     * The image URL is updated in the associated user document in Firestore.
     *
     * @param uri The URI of the image to load.
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


    //region FirebaseDataManagement

    // LiveData for the list of users.
    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>>
        get() = _userList

    // LiveData for Event ID.
    private val _eventId = MutableLiveData<String>()
    val eventId: LiveData<String>
        get() = _eventId

    // LiveData object for the current event.
    private val _currentEvent = MutableLiveData<Event>()
    val currentEvent: LiveData<Event>
        get() = _currentEvent

    // LiveData for the event image URL.
    private val _eventImageUrl = MutableLiveData<String>()
    val eventImageUrl: LiveData<String>
        get() = _eventImageUrl

    // LiveData for the like and dislike button.
    private val _isSelected = MutableLiveData<Boolean>()
    val isSelected: LiveData<Boolean>
        get() = _isSelected

    // Reference to the Firestore collection "events".
    val eventsRef = firestore.collection("events")

    // Reference to the Firestore collection "chats".
    val chatsRef = firestore.collection("chats")

    // Reference to the Firestore collection "user".
    val userRef = firestore.collection("user")


    //region FirebaseChatManagement

    /**
     * This feature creates a chat based on user ID.
     * The chat document is added to the Firestore chat collection.
     *
     * @param userId The user ID of the other participant in the chat.
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

    /**
     * Checks if a chat with the specified user already exists.
     * If a chat exists, it displays a toast message indicating that a chat with this user already exists.
     * If no chat exists, it creates a new chat with the specified user.
     *
     * @param userId The ID of the user to check for existing chat.
     */
    private fun checkIfChatExist(userId: String) {
        firestore.collection("chats")
            .whereArrayContains("userList", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    val chat = document.toObject(Chat::class.java)!!
                    if (chat.userList.contains(userId)) {
                        _toast.value = "A chat with this user already exists"
                        return@addOnSuccessListener
                    }
                }
                createChatById(userId)
            }
    }

    /**
     * This feature creates a chat based on email address.
     * Searching for a user with the specified email address.
     * When the user is found, it is checked whether a chat already exists with this user.
     * Otherwise, a corresponding error message will be displayed.
     *
     * @param email The email address of the other participant in the chat.
     */
    fun createChatByEmail(email: String) {
        userRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    if (email != auth.currentUser!!.email) {
                        val userId = it.documents[0].id
                        checkIfChatExist(userId)
                    } else {
                        _toast.value = "you can not add yourself"
                    }
                } else {
                    _toast.value = "e-mail not found"
                }
            }
    }

    /**
     * Creates a chat based on an event and an event ID.
     *
     * @param event The event object that creates the chat.
     * @param eventId The ID of the event the chat belongs to.
     */
    private fun createChatByEvent(event: Event, eventId: String) {
        val creator = event.creatorId
        val userList = listOf(creator)

        firestore.collection("chats")
            .add(Chat(userList = userList, group = true, eventId = eventId))
    }

    /**
     * Adds a user to the specified chat.
     *
     * @param chatId The ID of the chat to which the user is to be added.
     * @param userIdToAdd The ID of the user to be added to the chat.
     */
    fun addUserToChat(chatId: String, userIdToAdd: String) {
        chatsRef.document(chatId).update("userList", FieldValue.arrayUnion(userIdToAdd))
    }

    /**
     * Deletes a user from the specified chat.
     *
     * @param chatId The ID of the chat from which the user is to be deleted.
     * @param userIdToDelete The ID of the user to be deleted from the chat.
     */
    fun deleteUserFromChat(chatId: String, userIdToDelete: String) {
        chatsRef.document(chatId).update("userList", FieldValue.arrayRemove(userIdToDelete))
    }

    /**
     * Toggles the selection state.
     * If the current selection state is null, it sets the selection state to true; otherwise, it toggles the current selection state.
     */
    fun toggleSelection() {
        _isSelected.value = _isSelected.value?.not() ?: true
    }

    /**
     * This feature adds a message to a chat.
     * A new message with the content and sender ID will be created and added to the chat's "messages" collection.
     *
     * @param message The content of the message to be added.
     * @param chatId The ID of the chat to add the message to.
     */
    fun addMessageToChat(message: String, chatId: String) {
        val newMessage = Message(
            content = message,
            senderId = auth.currentUser!!.uid
        )
        firestore.collection("chats").document(chatId).collection("messages").add(newMessage)
    }

    /**
     * This function returns a reference to the collection of messages for a specific chat.
     *
     * @param chatId The ID of the chat for which to get the message reference.
     * @return The reference to the collection of messages for the specified chat.
     */
    fun getMessageRef(chatId: String): CollectionReference {
        return firestore.collection("chats").document(chatId).collection("messages")
    }

    /**
     * Deletes a chat with the specified chat ID.
     * If the chat is associated with an active event, it displays a toast message indicating that the event is still active.
     *
     * @param chatId The ID of the chat to be deleted.
     */
    fun deleteChat(chatId: String) {
        firestore.collection("chats").document(chatId)
            .get()
            .addOnSuccessListener { chatDoc ->
                val eventId = chatDoc.getString("eventId")
                if (eventId != null) {
                    eventsRef.document(eventId).get()
                        .addOnSuccessListener { eventDoc ->
                            if (eventDoc.exists()) {
                                _toast.value = "this event is still active!\nyou cannot delete it."
                            }
                        }
                } else {
                    firestore.collection("chats").document(chatId).collection("messages").get()
                        .addOnSuccessListener { messages ->
                            for (message in messages) {
                                message.reference.delete()
                            }
                        }
                    firestore.collection("chats").document(chatId).delete()
                }
            }
    }

    /**
     * Deletes all chats associated with the specified event ID.
     *
     * @param eventId The ID of the event for which chats are to be deleted.
     */
    private fun deleteChatByEventId(eventId: String) {
        firestore.collection("chats")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { chatQuerySnapshot ->
                for (chatDoc in chatQuerySnapshot.documents) {
                    val chatId = chatDoc.id
                    firestore.collection("chats").document(chatId).collection("messages").get()
                        .addOnSuccessListener { messages ->
                            for (message in messages) {
                                message.reference.delete()
                            }
                        }
                    firestore.collection("chats").document(chatId).delete()
                }
            }
    }

    //endregion


    //region FirebaseEventManagement

    /**
     * Updates the event ID in an event document in the Firestore database.
     *
     * @param eventId The ID of the event whose event ID should be updated.
     */
    private fun uploadEventId(eventId: String) {
        firestore.collection("events").document(eventId).update("eventId", eventId)
    }

    /**
     * Uploads a new event and its image to the Firestore database.
     * And creates a new chat based on an event
     *
     * @param event The event object to upload.
     * @param eventImage The URI of the event's image.
     */
    fun uploadEvent(event: Event, eventImage: Uri) {
        firestore.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                val eventId = documentReference.id
                _eventId.value = eventId
                uploadEventId(eventId)
                uploadEventImage(eventImage, eventId)
                createChatByEvent(event, eventId)
            }
    }

    /**
     * Updates an existing event and its image in the Firestore database.
     *
     * @param event The updated event object.
     * @param eventImage The URI of the event's updated image.
     * @param eventId The ID of the event to be updated.
     */
    fun updateEvent(event: Event, eventImage: Uri, eventId: String) {
        firestore.collection("events").document(eventId).set(event)
            .addOnSuccessListener {
                uploadEventImage(eventImage, eventId)
            }
    }

    /**
     * Clears the current event's data by placing an empty Event object in the LiveData object.
     * This causes all properties of the event to be set to empty strings.
     */
    fun clearEvent() {
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

    /**
     * Deletes the chat from the event and then the event with the specified event ID from the Firestore database.
     *
     * @param eventId The ID of the event to delete.
     */
    fun deleteEvent(eventId: String) {
        deleteChatByEventId(eventId)
        firestore.collection("events").document(eventId).delete()
    }

    /**
     * Uploads an event image to Firebase Storage and updates the image URL in the event document.
     *
     * @param uri The URI of the image to upload.
     * @param eventId The ID of the associated event.
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

    /**
     * Retrieves an event from the Firestore database by its ID and updates the LiveData object for the current event.
     *
     * @param eventId The ID of the event to retrieve.
     */
    fun getEventById(eventId: String) {
        if (eventId != "0") {
            eventsRef.document(eventId).get().addOnSuccessListener {
                val event = it.toObject<Event>()!!
                _currentEvent.value = event
            }
        }
    }

    //endregion

    //endregion


    //region API openstreetmap

    private val repository = Repository(BoostHubApi)

    // Create LiveData for the location from the API.
    val location = repository.location

    /**
     * Retrieves location information from the API and updates the LiveData object for the location.
     *
     * @param searchterm The search term for the location.
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