package com.example.films

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var topMenu: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        topMenu = findViewById(R.id.activity_main_top_menu)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            changeBottomNavigationViewVisibilityIfNeeded(destination.id)
            when (destination.id) {
                R.id.restFragment -> findViewById<TextView>(R.id.textView).text = "на русском языке"
                R.id.soapFragment -> findViewById<TextView>(R.id.textView).text = "на английском языке"
            }
        }
    }

    private fun changeBottomNavigationViewVisibilityIfNeeded(@IdRes destinationId: Int) {
        if (destinationId == R.id.restFragment || destinationId == R.id.soapFragment) {
            showBottomNavigationView()
        } else hideBottomNavigationView()
    }

    private fun showBottomNavigationView() {
        bottomNavigationView.visibility = View.VISIBLE
        topMenu.visibility = View.VISIBLE
        bottomNavigationView.animate().alpha(1.0f)
        topMenu.animate().alpha(1.0f)
    }

    private fun hideBottomNavigationView() {
        bottomNavigationView.visibility = View.INVISIBLE
        topMenu.visibility = View.INVISIBLE
        bottomNavigationView.alpha = 0.0f
        topMenu.alpha = 0.0f
    }
}