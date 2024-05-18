package com.example.merk
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var recoveryWordEditText: EditText
    private lateinit var verifyButton: Button
    private lateinit var userDao: UserDao
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_MERK_Dark_NoActionBar)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_MERK_Light_NoActionBar)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailEditText = findViewById(R.id.emailEditText)
        recoveryWordEditText = findViewById(R.id.recoveryWordEditText)
        verifyButton = findViewById(R.id.verifyButton)

        val db = UserDatabase.getDatabase(this)
        userDao = db.userDao()

        verifyButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val recoveryWord = recoveryWordEditText.text.toString().trim()

            GlobalScope.launch(Dispatchers.IO) {
                val user = userDao.getUserByEmail(email)
                withContext(Dispatchers.Main) {
                    if (user != null && user.recoveryWords.contains(recoveryWord)) {
                        ResetPasswordActivity.start(this@ForgotPasswordActivity, user.id)
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Неправильное кодовое слово или E-Mail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
