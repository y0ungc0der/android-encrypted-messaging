package ru.arina.mychat.registrationlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.arina.mychat.messages.LatestMessagesActivity
import com.example.mychat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ru.arina.mychat.models.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    companion object {
        private val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerButton = findViewById<View>(R.id.register_button_register)

        registerButton.setOnClickListener {

            Log.d(TAG, "Try to show the Login activity $registerButton")
            perfomRegister()

        }

        val photoButton = findViewById<View>(R.id.select_photo_button_register)

        photoButton.setOnClickListener {

            Log.d(TAG, "Try to show the Photo selector")

            val intent = Intent(Intent.ACTION_PICK)

            intent.type = "image/*"

            startActivityForResult(intent, 0)

        }


        val showLoginActivity = findViewById<View>(R.id.already_have_an_account_text_view)

        showLoginActivity.setOnClickListener {

            Log.d(TAG, "Try to show the Login activity")

            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPhotoUri = data.data
            Log.d(TAG, "Photo was selected. $selectedPhotoUri")

            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri)

            select_photo_button_register.alpha = 0f
            selectphoto_imageview_register.setImageBitmap(bitmap)
            //photoB.setBackground(getResources().getColor(R.color.purple_200))
        }
    }

    private fun perfomRegister() {
        val username = (findViewById<EditText>(R.id.username_edittext_register)).text.toString()
        val emailString = (findViewById<EditText>(R.id.email_edittext_register)).text.toString()
        val passwordString =
            (findViewById<EditText>(R.id.password_edittext_register)).text.toString()

        Log.d(TAG, "Email is: $emailString")
        Log.d(TAG, "Password is: $passwordString")

        if (username.isEmpty() || emailString.isEmpty() || passwordString.isEmpty()) {

            Log.d(TAG, "Failed to create a user. Not all fields are filled in.")
            Toast.makeText(
                applicationContext,
                "Failed to create a user. All lines must be filled in.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val result = task.result
                    if (result != null) {

                        val user = result.user
                        if (user != null) {

                            Log.d(TAG, "Successfully created user with uid: ${user.uid}")

                            uploadImageToFirebaseStorage()
                        }
                    }
                } else {
                    Log.d(TAG, "User creation failure. ${task.exception}")

                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    applicationContext,
                    "User creation failure. ${it.message}",
                    Toast.LENGTH_LONG
                ).show()

                return@addOnFailureListener
            }
    }

    private fun uploadImageToFirebaseStorage() {

        val imageURL: String =
            "https://firebasestorage.googleapis.com/v0/b/mychat-5bb73.appspot.com/o/images%2F76e549fa-76d2-4813-9898-6f49e5a79bf9?alt=media&token=207234cf-54c5-4fcb-96a8-0c026b5a07e2"

        if (selectedPhotoUri == null) {

            saveUserToFirebaseDatabase(imageURL)

        } else {

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {task ->

                    Log.d(TAG, "Successfully uploaded image: ${task.metadata?.path}")

                    ref.downloadUrl
                        .addOnSuccessListener {
                            it.toString()

                            Log.d(TAG, "File location: $it")

                            saveUserToFirebaseDatabase(it.toString())
                        }
                        .addOnFailureListener {

                            Log.d(
                                TAG,
                                "Downloading the URL of the user profile image failure. ${it.message}"
                            )

                            saveUserToFirebaseDatabase(imageURL)

                            return@addOnFailureListener
                        }
                }
                .addOnFailureListener {

                    Log.d(TAG, "Uploading the user profile image failure. ${it.message}")

                    return@addOnFailureListener
                }
        }
    }

    private fun saveUserToFirebaseDatabase(profileImageURL: String) {

        val username = (findViewById<EditText>(R.id.username_edittext_register)).text.toString()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid.toString(), username, profileImageURL)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally saved user to Firebase Database")

                val intent = Intent(this, LatestMessagesActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {

                Log.d(TAG, "Saving a user to the Firebase Database failure. ${it.message}")

                return@addOnFailureListener
            }
    }
}