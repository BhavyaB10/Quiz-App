package com.example.quiz_game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quiz_game.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding
    private val auth =FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityForgotPasswordBinding=ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view =activityForgotPasswordBinding.root
        setContentView(view)

        activityForgotPasswordBinding.buttonForgotPassword.setOnClickListener {

            val userEmail =activityForgotPasswordBinding.editTextForgotEmail.text.toString()

            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener {
                task->

                if(task.isSuccessful)
                {
                    Toast.makeText(applicationContext,"We sent a password reset mail to your mail address",Toast.LENGTH_SHORT).show()
                    finish()
                }else{

                    Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}