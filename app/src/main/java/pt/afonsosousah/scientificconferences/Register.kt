package pt.afonsosousah.scientificconferences

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Register button click listener
        val registerClick = findViewById<Button>(R.id.buttonRegister)
        registerClick.setOnClickListener {

            // Get the components
            val responseTextView = findViewById<TextView>(R.id.responseTextView)
            val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
            val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)

            // Create JSON request body
            val jsonRequest = JSONObject()
            jsonRequest.put("action", "register")
            jsonRequest.put("username", usernameEditText.text)
            jsonRequest.put("email", emailEditText.text)
            jsonRequest.put("password", passwordEditText.text)
            jsonRequest.put("confirm_password", confirmPasswordEditText.text)

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url = "http://10.0.2.2/schub/api/auth.php"

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,
                jsonRequest,
                { response ->
                    if (!response.getBoolean("success")) responseTextView.setTextColor(Color.RED)
                    else responseTextView.setTextColor(resources.getColor(R.color.light_gray))
                    responseTextView.text = response.getString("message")

                    if (response.getBoolean("success")) {
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    }
                },
                { error ->
                    responseTextView.setTextColor(Color.RED)
                    responseTextView.text = error.toString()
                }
            )

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest)
        }
    }
}