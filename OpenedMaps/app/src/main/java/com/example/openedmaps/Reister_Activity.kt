package com.example.openedmaps

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Reister_Activity : AppCompatActivity() {
    //variables
    private var isPasswordVisible = false
    private lateinit var tvTitle: TextView
    private lateinit var edUserName: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvTitleTwo: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnshowpass1: ImageView
    private lateinit var btnshowpass2: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reister)


        edUserName = findViewById(R.id.editTextTextEmailAddress2)
        edPassword = findViewById(R.id.editTextTextPassword2)
        edConfirmPassword = findViewById(R.id.editTextTextPassword3)
        btnRegister = findViewById(R.id.button5)
        tvTitleTwo = findViewById(R.id.textView2)
        btnshowpass1 = findViewById(R.id.imageView)
        btnshowpass2 = findViewById(R.id.imageView2)

        mAuth = FirebaseAuth.getInstance()

        //hide password methods

        btnshowpass1.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Show password
                edConfirmPassword.transformationMethod = null
                btnshowpass1.setImageResource(R.drawable.ic_eye)
            } else {
                // Hide password
                edConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnshowpass1.setImageResource(R.drawable.ic_eye_closed)
            }
        }
        btnshowpass2.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Show password
                edPassword.transformationMethod = null
                btnshowpass2.setImageResource(R.drawable.ic_eye)
            } else {
                // Hide password
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnshowpass2.setImageResource(R.drawable.ic_eye_closed)
            }


        }//pass 2 hide ends

        btnRegister.setOnClickListener { view ->
            RegUser(view)
        }

    }//onCreate ends

    fun LoginPage(view: View) {
        val intent = Intent(this, Login_Activity::class.java)
        startActivity(intent)
    }

    fun isPasswordValid(password: String): Boolean {
        val minLength = 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { it.isLetterOrDigit().not() }

        return password.length >= minLength &&
                hasUppercase && hasLowercase &&
                hasDigit && hasSpecialChar
    }
    fun RegUser(view: View) {
        if (view.id == R.id.button5) {
            val email = edUserName.text.toString().trim()
            val passwordReg = edPassword.text.toString().trim()
            val conPassword = edConfirmPassword.text.toString().trim()
            val newPassword = "SecureP@ssw0rd"


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email cant be blank", Toast.LENGTH_SHORT).show()
                return
            }

            if (TextUtils.isEmpty(passwordReg)) {
                Toast.makeText(this, "Password cant be blank", Toast.LENGTH_SHORT).show()
                return
            }

            if (TextUtils.isEmpty(conPassword)) {
                Toast.makeText(this, "Confirm Password cant be blank", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (conPassword == passwordReg) {
                mAuth.createUserWithEmailAndPassword(email, passwordReg)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, Login_Activity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to Register",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (isPasswordValid(newPassword)) {
                            Toast.makeText(
                                this,
                                "Password has to contain 1 lowercase, 1 uppercase, a number and a special character",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }




    }

}