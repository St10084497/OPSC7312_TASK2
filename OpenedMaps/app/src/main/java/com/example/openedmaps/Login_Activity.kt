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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class Login_Activity : AppCompatActivity() {
    private var isPasswordVisible = false

    private lateinit var btnShowPass: ImageButton
    private lateinit var edUserName: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnRegister: Button


    private lateinit var tvHeadingTwo: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edUserName = findViewById(R.id.editTextTextEmailAddress)
        edPassword = findViewById(R.id.editTextTextPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnRegister = findViewById(R.id.btnReg)


        btnShowPass=findViewById(R.id.showPasswordButton)


        btnShowPass.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Show password
                edPassword.transformationMethod = null
                btnShowPass.setImageResource(R.drawable.ic_eye)
            } else {
                // Hide password
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnShowPass.setImageResource(R.drawable.ic_eye_closed)
            }

            // Move cursor to the end of the password field
            edPassword.setSelection(edPassword.text.length)
        }

        //reg page nav
        btnRegister.setOnClickListener{
            val intent = Intent(this, Reister_Activity::class.java)
            startActivity(intent)
        }
        mAuth = FirebaseAuth.getInstance()
    }
    fun login(view: View) {
        try {
            val email = edUserName.text.toString().trim()
            val password = edPassword.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) )
            {
                Toast.makeText(this, "Enter a valid email or password !!!", Toast.LENGTH_SHORT).show()

            }
            else {


                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@Login_Activity,
                                "Logged in Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@Login_Activity, Birds_Activity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@Login_Activity,
                                "Login Failed!!! Username or Password is incorrect",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


}