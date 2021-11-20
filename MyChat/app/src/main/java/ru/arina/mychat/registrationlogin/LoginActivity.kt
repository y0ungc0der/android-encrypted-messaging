package ru.arina.mychat.registrationlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mychat.R
import ru.arina.mychat.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {
        private val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<View>(R.id.login_button_login)

        loginButton.setOnClickListener {

            performLogin()
        }

        val backToMainActivity = findViewById<View>(R.id.back_to_register_textview)

        backToMainActivity.setOnClickListener {

            Log.d(TAG, "Try to go back to the Main activity")

            finish()
        }
    }

    private fun performLogin() {
        val email = findViewById<EditText>(R.id.email_edittext_login)
        val password = findViewById<EditText>(R.id.password_edittext_login)
        val emailString = email.text.toString()
        val passwordString = password.text.toString()

        Log.d(TAG, "Email is: " + emailString)
        Log.d(TAG, "Password is: " + passwordString)

        if (emailString.isEmpty() || passwordString.isEmpty()) {

            Log.d(TAG, "Failed to log in. Not all fields are filled in.")
            Toast.makeText(
                applicationContext,
                "Failed to log in. All lines must be filled in.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailString, passwordString)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val result = task.result
                    if (result != null) {

                        val user = result.user
                        if (user != null) {

                            Log.d("Login", "Successfully logged in: ${user.uid}")

                            val intent = Intent(this, LatestMessagesActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("MainActivity", "User creation failure. ${task.exception}")
                    return@addOnCompleteListener
                }
            }

            .addOnFailureListener {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    applicationContext,
                    "Failed to log in: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()

                return@addOnFailureListener
            }
    }
}