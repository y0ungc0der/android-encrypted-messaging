package ru.arina.mychat.views

import com.example.mychat.R
import ru.arina.mychat.models.ChatMessage
import ru.arina.mychat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*


class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        // Расшифровка нужна?
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        val chatPartnerID: String
        if (chatMessage.fromID == FirebaseAuth.getInstance().uid) {
            chatPartnerID = chatMessage.toID

        } else {

            chatPartnerID = chatMessage.fromID
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                chatPartnerUser = snapshot.getValue(User::class.java)

                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username
                Picasso.get().load(chatPartnerUser?.profileImageURL)
                    .into(viewHolder.itemView.imageview_latest_message)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {

        return R.layout.latest_message_row
    }
}