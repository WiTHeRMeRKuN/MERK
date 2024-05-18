package com.example.merk

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var forgotPasswordText: TextView
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @SuppressLint("ServiceCast")
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

        val recoveryWords = intent.getStringArrayListExtra("recoveryWords")
        recoveryWords?.let {
            val wordsText = it.joinToString(", ")
            lifecycleScope.launch {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Ваши кодовые слова", Snackbar.LENGTH_INDEFINITE)
                snackbar.setAction("Копировать") {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Recovery Words", wordsText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this@LoginActivity, "Слова скопированы", Toast.LENGTH_SHORT).show()
                }
                snackbar.show()
                delay(5000)
                snackbar.dismiss()
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        setupBiometricAuthentication()

        loginButton.setOnClickListener {
            authenticateUser()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        checkLogin()
    }

    private fun setupBiometricAuthentication() {
        biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Ошибка аутентификации: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                login(usernameEditText.text.toString(), passwordEditText.text.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Не удалось выполнить проверку подлинности", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Биометрический вход в систему МЕРК")
            .setNegativeButtonText("                ")

            .build()
    }

    private fun authenticateUser() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun login(username: String, password: String) {
        val userDao = UserDatabase.getDatabase(this).userDao()

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                userDao.getUserByCredentials(username, password)
            }

            if (user != null) {
                if (rememberMeCheckBox.isChecked) {
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("is_logged_in", true)
                    editor.putString("user_role", user.role)
                    editor.apply()
                }
                val intent = when (user.role) {
                    "Клиент" -> Intent(this@LoginActivity, MainClientActivity::class.java)
                    "Сотрудник" -> Intent(this@LoginActivity, MainEmployeeActivity::class.java)
                    else -> throw IllegalStateException("Неизвестная роль")
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val role = sharedPreferences.getString("user_role", "")
            val intent = when (role) {
                "Клиент" -> Intent(this, MainClientActivity::class.java)
                "Сотрудник" -> Intent(this, MainEmployeeActivity::class.java)
                else -> throw IllegalStateException("Неизвестная роль")
            }
            startActivity(intent)
            finish()
        }
    }
}
