package com.example.simple

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUp : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mDbRef: DatabaseReference

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.email)
        edtName = findViewById(R.id.Name)
        edtPassword = findViewById(R.id.password)
        btnSignUp = findViewById(R.id.signup)

        btnSignUp.setOnClickListener{
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val pass = edtPassword.text.toString()

            signup(name,email,pass)
        }


    }

    private fun signup(name:String,email:String,pass:String)
    {
        mAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    adduserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    val intent = Intent(this@SignUp,MainActivity::class.java )
                    finish()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@SignUp,"Enter Correct Email or Password",Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun adduserToDatabase(name: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(name,email,uid))


    }


}