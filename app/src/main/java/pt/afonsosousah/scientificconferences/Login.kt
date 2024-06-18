package pt.afonsosousah.scientificconferences

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val openRegisterTextView = findViewById<TextView>(R.id.buttonOpenRegister)
        val paint = openRegisterTextView.paint
        val width = paint.measureText(openRegisterTextView.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, openRegisterTextView.textSize, intArrayOf(
            Color.parseColor("#03fbab"),
            Color.parseColor("#1ddcff")
        ), null, Shader.TileMode.REPEAT)
        openRegisterTextView.paint.setShader(textShader)

        // Open register click listener
        openRegisterTextView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Login button click listener
        val loginClick = findViewById<Button>(R.id.submitButton)
        loginClick.setOnClickListener {

            // Get the components
            val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

            // Create JSON request body
            val jsonRequest = JSONObject()
            jsonRequest.put("action", "login")
            jsonRequest.put("username_or_email", usernameEditText.text)
            jsonRequest.put("password", passwordEditText.text)

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url = "http://10.0.2.2/schub/api.php"

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url,
                jsonRequest,
                { response ->
                    if (response.getBoolean("success")) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        val sharedPref = getSharedPreferences("User", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("username", response.getString("username"))
                            putInt("user_id", response.getInt("id"))
                            putBoolean("isLoggedIn", true)
                            apply()
                        }
                    }
                    else {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                },
                { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                }
            )

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)
        }
    }
}