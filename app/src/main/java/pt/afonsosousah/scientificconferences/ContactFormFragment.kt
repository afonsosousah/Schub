package pt.afonsosousah.scientificconferences

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFormFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_contact_form, container, false)

        val sharedPref = rootView.context.getSharedPreferences("User", Context.MODE_PRIVATE)
        val submitButton = rootView.findViewById<AppCompatButton>(R.id.submitButton)
        val topicEditText = rootView.findViewById<EditText>(R.id.topicMultiLine)
        val descriptionEditText = rootView.findViewById<EditText>(R.id.descriptionMultiLine)

        submitButton.setOnClickListener {
            val queue = Volley.newRequestQueue(activity?.applicationContext)
            val url = getString(R.string.endpoint) + "/api"
            val sendQuestionJsonRequest = JSONObject()
            sendQuestionJsonRequest.put("action", "sendContact")
            sendQuestionJsonRequest.put("user_id", sharedPref.getInt("user_id", 0))
            sendQuestionJsonRequest.put("topic", topicEditText.text)
            sendQuestionJsonRequest.put("description", descriptionEditText.text)
            val sendQuestionJsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url,
                sendQuestionJsonRequest,
                { response ->
                    Toast.makeText(activity?.applicationContext, response.getString("message"), Toast.LENGTH_LONG).show()
                    replaceFragment(HomeFragment())
                },
                { error ->
                    Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                }
            )
            queue.add(sendQuestionJsonObjectRequest)
        }

        return rootView
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_contactForm.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFormFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}