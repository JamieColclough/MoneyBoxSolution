package com.example.minimoneybox

import android.animation.Animator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.Button
import android.widget.EditText
import com.airbnb.lottie.LottieAnimationView
import java.util.regex.Pattern
import com.airbnb.lottie.LottieDrawable
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import com.example.minimoneybox.webAPI.LoginAPI
import android.preference.PreferenceManager
import android.content.SharedPreferences




/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    lateinit var btn_sign_in : Button
    lateinit var til_email : TextInputLayout
    lateinit var et_email : EditText
    lateinit var til_password : TextInputLayout
    lateinit var et_password : EditText
    lateinit var til_name : TextInputLayout
    lateinit var et_name : EditText
    lateinit var pigAnimation : LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupViews()
    }

    override fun onStart() {
        super.onStart()
        setupAnimation()
    }

    private fun setupViews() {
        btn_sign_in = findViewById(R.id.btn_sign_in)
        til_email = findViewById(R.id.til_email)
        et_email = findViewById(R.id.et_email)
        til_password = findViewById(R.id.til_password)
        et_password = findViewById(R.id.et_password)
        til_name = findViewById(R.id.til_name)
        et_name = findViewById(R.id.et_name)
        pigAnimation = findViewById(R.id.animation)

        btn_sign_in.setOnClickListener {
            if (allFieldsValid()) {
                val loginAPI = LoginAPI(applicationContext)
                val email = et_email.text.toString()
                val password = et_password.text.toString()
                val name = et_name.text.toString()
                val sp = PreferenceManager.getDefaultSharedPreferences(this).edit()
                sp.remove("name"); //Removes stored name if new sign in

                if(name.isNotEmpty()){
                    sp.putString("name",name)
                }
                sp.apply()

                loginAPI.execute(email, password)

            }
        }


    }

    private fun allFieldsValid() : Boolean {
        var allValid = true //Set to true by default, if issue with field, will set to false
        //Sets error messages to blank by default, allows error to clear if correct text entered
        til_email.error = ""
        til_password.error = ""
        til_name.error = ""

        if (!Pattern.matches(EMAIL_REGEX, et_email.text.toString())) {
            allValid = false
            til_email.error = getString(R.string.email_address_error)
        }

        if (!Pattern.matches(PASSWORD_REGEX, et_password.text.toString())) {
            allValid = false
            til_password.error = getString(R.string.password_error)
        }

        var firstName = et_name.text.toString()

        if (firstName.isNotEmpty() && !Pattern.matches(NAME_REGEX, firstName)) {
            allValid = false
            til_name.error = getString(R.string.full_name_error)
        }

        return allValid
    }

    private fun setupAnimation() {
        pigAnimation.repeatCount = 0
        pigAnimation.setMinAndMaxFrame(0, 109)

        pigAnimation.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    loopAnimation()
                }
            })

        pigAnimation.playAnimation()

    }

    private fun loopAnimation() {
        pigAnimation.setMinAndMaxFrame(131, 158)
        pigAnimation.repeatCount = LottieDrawable.INFINITE
        pigAnimation.playAnimation()

    }

    companion object {
        val EMAIL_REGEX = "[^@]+@[^.]+\\..+"
        val NAME_REGEX = "[a-zA-Z]{6,30}"
        val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z]).{10,50}$"
    }
}
