package com.example.quiz_game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.quiz_game.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding

    private val auth :FirebaseAuth =FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding=ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)

        signUpBinding.buttonSignUp.setOnClickListener {

            val email = signUpBinding.editTextSignUpEmail.text.toString()
            val password =signUpBinding.editTextSignUpPassword.text.toString()

            signUpWithFirebase(email,password)

        }
    }

    private fun signUpWithFirebase(email:String, password:String)
    {
        signUpBinding.progressBar.visibility= View.VISIBLE

        signUpBinding.buttonSignUp.isClickable=false

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->

            if(task.isSuccessful)
            {
               Toast.makeText(applicationContext,"Your account has been created",Toast.LENGTH_SHORT).show()
                signUpBinding.progressBar.visibility=View.INVISIBLE
                signUpBinding.buttonSignUp.isClickable=true
                finish()

            }
            else
            {
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }
}