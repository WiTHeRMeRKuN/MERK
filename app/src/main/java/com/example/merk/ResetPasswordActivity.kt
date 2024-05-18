package com.example.merk

import android.content.Context
import android.content.Intent
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

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var newPasswordEditText: EditText
    private lateinit var saveButton: Button
    private var userId: Int = 0
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
        setContentView(R.layout.activity_reset_password)

        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        saveButton = findViewById(R.id.saveButton)
        userId = intent.getIntExtra("user_id", 0)
        val db = UserDatabase.getDatabase(this)
        userDao = db.userDao()

        saveButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString().trim()
            if (newPassword.isNotBlank()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val user = userDao.getUserById(userId)
                    user?.let {
                        it.password = newPassword
                        userDao.updateUser(it)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ResetPasswordActivity, "Пароль успешно обновлен", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите новый пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun start(context: Context, userId: Int) {
            val intent = Intent(context, ResetPasswordActivity::class.java)
            intent.putExtra("user_id", userId)
            context.startActivity(intent)
        }
    }
}
