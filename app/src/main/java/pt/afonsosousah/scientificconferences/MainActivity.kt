package pt.afonsosousah.scientificconferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bottom menu
        val bottomNavigationMenu = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationMenu.itemIconTintList = null; // for gradient icons
        bottomNavigationMenu.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.scheduleMenu -> {
                    replaceFragment(ScheduleFragment())
                    true
                }
                R.id.articlesMenu -> {
                    replaceFragment(ArticleListFragment())
                    true
                }
                R.id.sessionsMenu -> {
                    replaceFragment(SessionListFragment())
                    true
                }
                R.id.mapMenu -> {
                    replaceFragment(MapsFragment())
                    true
                }
                R.id.homeMenu -> {
                    replaceFragment(HomeFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(HomeFragment())

        // Get user info
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
        //val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        usernameTextView.text = "Welcome, " + username
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }
}