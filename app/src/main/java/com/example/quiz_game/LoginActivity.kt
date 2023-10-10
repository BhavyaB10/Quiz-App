package com.example.quiz_game

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz_game.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding

    private val auth =FirebaseAuth.getInstance()

    private lateinit var googleSignInClient : GoogleSignInClient

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding=ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)
        val textofGoogleButton = loginBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        textofGoogleButton.text = "Continue with google"
        textofGoogleButton.setTextColor(Color.BLACK)
        textofGoogleButton.textSize = 18f

        //register
        registerActivityForGoogleSignIn()


        loginBinding.buttonSignIn.setOnClickListener {

            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword =loginBinding.editTextLoginPassword.text.toString()

            signInUser(userEmail,userPassword)

        }

        loginBinding.buttonGoogleSignIn.setOnClickListener {

            signInGoogle()


        }

        loginBinding.textViewSignUp.setOnClickListener {

            val intent = Intent(this@LoginActivity,SignUpActivity::class.java)
            startActivity(intent)
            finish()

        }
        loginBinding.textViewForgotPassword.setOnClickListener {

            val intent=Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)

        }
    }

    private fun signInUser(userEmail : String, userPassword : String)
    {
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener {
            task->

            if(task.isSuccessful)
            {
                Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
               val intent =Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val user =auth.currentUser

        if(user!=null)
        {
            Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
            val intent =Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun signInGoogle(){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("843320987018-kcku897vdp3kscbvmro161a8c5jfsb43.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        signIn()
    }

    private fun signIn()
    {
        val signIntent :Intent =googleSignInClient.signInIntent
        activityResultLauncher.launch(signIntent)

    }

    private fun registerActivityForGoogleSignIn(){

        activityResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == RESULT_OK && data != null) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                firebaseSignInWithGoogle(task)

            }


        }


    }

    private fun firebaseSignInWithGoogle(task :Task<GoogleSignInAccount>){

        try {
            val account :GoogleSignInAccount =task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext,"Welcome to quiz game ",Toast.LENGTH_SHORT).show()

            val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

            fireBaseGoogleAccount(account)
        }
        catch(e: ApiException){

            Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }


    }
    private fun fireBaseGoogleAccount(account : GoogleSignInAccount){

        val authCredential =GoogleAuthProvider.getCredential(account.idToken,null)

        auth.signInWithCredential(authCredential).addOnCompleteListener {
            task->

//            if(task.isSuccessful)
//            {
//            //  val user = auth.currentUser
//
//            }
//            else{
//
//            }
        }
    }
}