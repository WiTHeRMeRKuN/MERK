package com.example.merk

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivityssss : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var lastNameEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var dateOfBirthButton: Button
    private var selectedDateOfBirth: String? = null
    private lateinit var genderSpinner: Spinner
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var userDao: UserDao

    @RequiresApi(Build.VERSION_CODES.N)
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
        setContentView(R.layout.activity_register)

        lastNameEditText = findViewById(R.id.lastNameEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        middleNameEditText = findViewById(R.id.middleNameEditText)
        dateOfBirthButton = findViewById(R.id.dateOfBirthButton)
        dateOfBirthButton = findViewById(R.id.dateOfBirthButton)
        dateOfBirthButton.setOnClickListener {
            showDatePickerDialog()
        }
        genderSpinner = findViewById(R.id.genderSpinner)
        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        roleSpinner = findViewById(R.id.roleSpinner)
        registerButton = findViewById(R.id.registerButton)

        val db = UserDatabase.getDatabase(this)
        userDao = db.userDao()

        registerButton.setOnClickListener {
            val lastName = lastNameEditText.text.toString().trim()
            val firstName = firstNameEditText.text.toString().trim()
            val middleName = middleNameEditText.text.toString().trim()
            val dob = dateOfBirthButton.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = roleSpinner.selectedItem.toString()

            if (validateInputs()) {
                val recoveryWords = generateRecoveryWords()
                val user = User(
                    id = 0,
                    lastName = lastName,
                    firstName = firstName,
                    middleName = middleName,
                    dob = dob,
                    gender = gender,
                    username = username,
                    email = email,
                    phone = phone,
                    password = password,
                    role = role,
                    recoveryWords = recoveryWords
                )

                GlobalScope.launch(Dispatchers.IO) {
                    userDao.insertUser(user)
                    withContext(Dispatchers.Main) {
                      Toast.makeText(this@RegisterActivityssss, "Регистрация завершена. Пожалуйста, войдите в систему.", Toast.LENGTH_LONG).show()
                        delay(1000)
                        val intent = Intent(this@RegisterActivityssss, LoginActivity::class.java)
                        intent.putStringArrayListExtra("recoveryWords", ArrayList(recoveryWords))
                        startActivity(intent)
                        finish()
                    }
                }
           }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            selectedDateOfBirth = String.format("%02d.%02d.%d", dayOfMonth, monthOfYear + 1, year)
            dateOfBirthButton.text = selectedDateOfBirth
        }, year, month, day)

        dpd.show()
    }

    private fun validateInputs(): Boolean {
        if (lastNameEditText.text.isBlank() ||
            firstNameEditText.text.isBlank() ||
            middleNameEditText.text.isBlank() ||
            dateOfBirthButton.text.isBlank() ||
            usernameEditText.text.isBlank() ||
            emailEditText.text.isBlank() ||
            phoneEditText.text.isBlank() ||
            passwordEditText.text.isBlank()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun generateRecoveryWords(): List<String> {
        val words = listOf("appel", "baanna", "cehrry", "daet", "eldreberry", "gif", "garpe", "hoenydew", "ikwi", "lemno", "mnago", "necatrine", "ornage", "papyaa", "quinec", "raspebrry", "strawbrery", "tangreine", "ugil", "voilet", "watremelon", "xgiua", "yelolw", "zuccihni")
        return words.shuffled().take(6)
    }
}
