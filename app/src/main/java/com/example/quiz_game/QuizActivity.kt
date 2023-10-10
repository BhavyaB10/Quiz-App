package com.example.quiz_game

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.quiz_game.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    lateinit var quizBinding: ActivityQuizBinding

    private val dataBase = FirebaseDatabase.getInstance()
    private val databaseReference = dataBase.reference.child("questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount = 0
    var questionNumber = 0

    private var userAnswer = ""
    private var userCorrect = 0
    private var userWrong = 0

    private lateinit var timer : CountDownTimer
    private val totalTime = 25000L

    var timerContinue =false
    var leftTime = totalTime

    private val auth = FirebaseAuth.getInstance()
    private val user =auth.currentUser

    private val scoreRef=dataBase.reference

    val questions=HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do{
           val number = Random.nextInt(1,11)

            Log.d("number",number.toString())
            questions.add(number)
        }while(questions.size < 5)

        Log.d("numberOfQuestions",questions.toString())


        gameLogic()

        quizBinding.buttonNext.setOnClickListener {

            resetTimer()
            gameLogic()


        }

        quizBinding.buttonFinish.setOnClickListener {

          sendScore()

        }

        quizBinding.textViewA.setOnClickListener {

              userAnswer = "a"
            pauseTimer()

            if(correctAnswer==userAnswer)
            {
                quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++

                quizBinding.textViewCorrect.text=userCorrect.toString()

            }else{
                quizBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++

                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOption()
        }

        quizBinding.textViewB.setOnClickListener {

            pauseTimer()
            userAnswer = "b"


            if(correctAnswer==userAnswer)
            {
                quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++

                quizBinding.textViewCorrect.text=userCorrect.toString()
            }else{
                quizBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++

                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOption()
        }

        quizBinding.textViewC.setOnClickListener {
            pauseTimer()
            userAnswer = "c"


            if(correctAnswer==userAnswer)
            {
                quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++

                quizBinding.textViewCorrect.text=userCorrect.toString()
            }else{
                quizBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++

                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOption()

        }

        quizBinding.textViewD.setOnClickListener{
            pauseTimer()

            userAnswer = "d"


            if(correctAnswer==userAnswer)
            {
                quizBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++

                quizBinding.textViewCorrect.text=userCorrect.toString()
            }else{
                quizBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++

                quizBinding.textViewWrong.text=userWrong.toString()
                findAnswer()
            }
            disableClickableOption()

        }
 }

        private fun gameLogic() {
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    questionCount = snapshot.childrenCount.toInt()

                    if (questionNumber < questions.size) {
                        question = snapshot.child(questions.elementAt(questionNumber).toString()).child("q").value.toString()
                        answerA = snapshot.child(questions.elementAt(questionNumber).toString()).child("a").value.toString()
                        answerB = snapshot.child(questions.elementAt(questionNumber).toString()).child("b").value.toString()
                        answerC = snapshot.child(questions.elementAt(questionNumber).toString()).child("c").value.toString()
                        answerD = snapshot.child(questions.elementAt(questionNumber).toString()).child("d").value.toString()
                        correctAnswer = snapshot.child(questions.elementAt(questionNumber).toString()).child("answer").value.toString()

                        quizBinding.textViewQuestion.text = question
                        quizBinding.textViewA.text = answerA
                        quizBinding.textViewB.text = answerB
                        quizBinding.textViewC.text = answerC
                        quizBinding.textViewD.text = answerD
                      //  quizBinding.textViewCorrect.text = correctAnswer

                        quizBinding.progressBar2.visibility = View.INVISIBLE
                        quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                        quizBinding.linearLayoutQuestion.visibility = View.VISIBLE
                        quizBinding.linearLayoutButton.visibility = View.VISIBLE

                        startTimer()
                    } else {
                       val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                        dialogMessage.setTitle("Quiz Game")
                        dialogMessage.setMessage("Congratulation!!\nYou have answer all the question. Do you want to see the result ?")
                        dialogMessage.setCancelable(false)
                        dialogMessage.setPositiveButton("See result"){dialogWindow,position ->

                            sendScore()
                        }

                        dialogMessage.setNegativeButton("Play Again"){ dialogWindow,position ->

                            val intent=Intent(this@QuizActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        dialogMessage.create().show()
                    }

                    restoreOptions()
                    questionNumber++

                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun findAnswer() {

            when (correctAnswer) {
                "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
                "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
                "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
                "d" -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
            }
        }

        fun disableClickableOption() {
            quizBinding.textViewA.isClickable = false
            quizBinding.textViewB.isClickable = false
            quizBinding.textViewC.isClickable = false
            quizBinding.textViewD.isClickable = false
        }

        fun restoreOptions() {
            quizBinding.textViewA.setBackgroundColor(Color.WHITE)
            quizBinding.textViewB.setBackgroundColor(Color.WHITE)
            quizBinding.textViewC.setBackgroundColor(Color.WHITE)
            quizBinding.textViewD.setBackgroundColor(Color.WHITE)

            quizBinding.textViewA.isClickable = true
            quizBinding.textViewB.isClickable = true
            quizBinding.textViewC.isClickable = true
            quizBinding.textViewD.isClickable = true
        }

    fun startTimer(){

        timer=object : CountDownTimer(leftTime,1000){
            override fun onTick(millisUntilFinish: Long) {
                leftTime=millisUntilFinish
                updateCountDownText()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {

                disableClickableOption()
                resetTimer()
                updateCountDownText()

                quizBinding.textViewQuestion.text = "Sorry time is up continue with the next Question!!"
                timerContinue=false
            }

        }.start()

        timerContinue=true

    }

    fun updateCountDownText(){

        val remainingTime :Int = (leftTime/1000).toInt()
        quizBinding.textViewTime.text=remainingTime.toString()

    }

    private fun pauseTimer(){

        timer.cancel()
        timerContinue = false

    }

    fun resetTimer(){

        pauseTimer()
        leftTime=totalTime
        updateCountDownText()

    }

    fun sendScore(){
        user?.let {
            val userUID = it.uid
            scoreRef.child("score").child(userUID).child("correct").setValue(userCorrect)

            scoreRef.child("score").child(userUID).child("wrong").setValue(userWrong).addOnSuccessListener {
                Toast.makeText(applicationContext,"Score sent to database successfully",Toast.LENGTH_SHORT).show()

                val intent= Intent(this@QuizActivity,ResultActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
    }