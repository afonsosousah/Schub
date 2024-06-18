package pt.afonsosousah.scientificconferences

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Get the updated information from API
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = getString(R.string.endpoint) + "/api";
        val infoJsonRequest = JSONObject()
        infoJsonRequest.put("action", "getEntries")
      infoJsonRequest.put("table", "information")
        val infoJsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,
            infoJsonRequest,
            { response ->
                val infoArrayJSON = response.getJSONArray("response")
                val infoObjectJSON = infoArrayJSON.getJSONObject(0)

                val startDate = infoObjectJSON.getString("start_date")
                val endDate = infoObjectJSON.getString("end_date")
                val datesString = "from ${startDate} until ${endDate}"

                // Populate values
                view?.findViewById<TextView>(R.id.ConferenceNameTV)?.text = infoObjectJSON.getString("conference_name")
                view?.findViewById<TextView>(R.id.LocationTV)?.text = infoObjectJSON.getString("location")
                view?.findViewById<TextView>(R.id.TransportTV)?.text = infoObjectJSON.getString("transport")
                view?.findViewById<TextView>(R.id.ContactsTV)?.text = infoObjectJSON.getString("contacts")
                view?.findViewById<TextView>(R.id.CodeConductTV)?.text = infoObjectJSON.getString("code_of_conduct")
                view?.findViewById<TextView>(R.id.DatesTV)?.text = datesString
            },
            { error ->
                Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(infoJsonObjectRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

