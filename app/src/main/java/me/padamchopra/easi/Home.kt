package me.padamchopra.easi

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Error
import java.util.*

class Home : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth
        val sharedPreferences = getSharedPreferences("EASI", Context.MODE_PRIVATE)
        if (auth.currentUser == null || sharedPreferences.getString("uid", null) == null) {
            //Sign in anonymously
            auth.signInAnonymously().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val editor = sharedPreferences.edit()
                    editor.putString("uid", user!!.uid)
                    editor.apply()
                } else {
                    Snackbar.make(
                        baseContext,
                        findViewById(R.id.home_mic_btn),
                        "Failed to connect to server. Try again.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val micBtn = findViewById<MaterialButton>(R.id.home_mic_btn)
        micBtn.setOnClickListener { view -> startMic(view) }
    }

    private fun startMic(view: View) {
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening")
        try {
            startActivityForResult(sttIntent, 1)
        } catch (e: Error) {
            e.printStackTrace()
            Snackbar.make(view, "Error in starting speech recognition. Try again.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!result.isNullOrEmpty()) {
                        val recognizedText = result[0]
                        print(recognizedText)
                    }
                }
            }
        }
    }
}