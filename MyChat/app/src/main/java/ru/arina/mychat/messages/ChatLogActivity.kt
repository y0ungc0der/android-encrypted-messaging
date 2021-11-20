package ru.arina.mychat.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mychat.R
import ru.arina.mychat.models.ChatMessage
import ru.arina.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import ru.arina.mychat.views.ChatFromItem
import ru.arina.mychat.views.ChatToItem

class ChatLogActivity : AppCompatActivity() {

    companion object {
        private val TAG = "ChatLogActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username

        listenForMessages()

        send_button_chat_log.setOnClickListener {

            Log.d(TAG, "Try to send message...")

            sendMessage()
        }
    }

    private fun sendMessage() {
        // Здесь добавить шифрование для text
        val fromId = FirebaseAuth.getInstance().uid
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = toUser?.uid
        val text = edittext_chat_log.text.toString()

        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val fromRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        if (fromId == null || toId == null) return

        val chatMessage =
            ChatMessage(fromRef.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        fromRef.setValue(chatMessage)
            .addOnSuccessListener {

                Log.d(TAG, "Save our chat message for $fromId: ${fromRef.key}")

                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1  )
            }

        toRef.setValue(chatMessage)
            .addOnSuccessListener {

                Log.d(TAG, "Save our chat message for $toId: ${toRef.key}")
            }

        val toLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        val fromLatestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")

        toLatestMessageRef.setValue(chatMessage)
        fromLatestMessageRef.setValue(chatMessage)
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        // Здесь добавить  расшифрование для text
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromID == FirebaseAuth.getInstance().uid) {

                        val curUser = LatestMessagesActivity.currentUser ?: return

                        adapter.add(ChatFromItem(chatMessage.text, curUser))

                    } else {

                        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }
}
