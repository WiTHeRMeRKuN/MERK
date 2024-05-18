package com.example.merk

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainClientActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout

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
        setContentView(R.layout.activity_main_client)

        val toolbar: Toolbar = findViewById(R.id.ClientToolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        val navigationView: NavigationView = findViewById(R.id.nav_client_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dark_mode -> {
                    showThemeChooser()
                    true
                }
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawer_layout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showThemeChooser() {
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        val choices = arrayOf("Темная тема", "Светлая тема")
        val currentChoice = if (isDarkMode) 0 else 1
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите тему:")
        builder.setSingleChoiceItems(choices, currentChoice) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPreferences.edit().putBoolean("dark_mode", true).apply()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferences.edit().putBoolean("dark_mode", false).apply()
                }
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.apply()
        drawerLayout.closeDrawer(GravityCompat.START)
        val logoutIntent = Intent(this, LoginActivity::class.java)
        startActivity(logoutIntent)
        finish()
    }

    private fun restartActivity() {
        startActivity(Intent(this, MainClientActivity::class.java))
        finish()
        overridePendingTransition(0, 0)
    }
}