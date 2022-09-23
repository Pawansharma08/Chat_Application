package com.example.simple

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecycleView:RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: Message_Adapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef : DatabaseReference

    private var receiverRoom : String? = null
    private var senderRoom : String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        chatRecycleView = findViewById(R.id.ChatRecycleView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        messageList = ArrayList()
        messageAdapter = Message_Adapter(this,messageList)

        chatRecycleView.layoutManager = LinearLayoutManager(this)
        chatRecycleView.adapter = messageAdapter

        //LOGIc FOR ADDING DATA IN RECYCLE VIEw

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid  = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name


    mDbRef.child("chats").child(senderRoom!!).child("messages")
        .addValueEventListener(object : ValueEventListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()

                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        sendButton.setOnClickListener{

            val message = messageBox.text.toString()
            val messageObject = Message(message,senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")
        }
    }
}