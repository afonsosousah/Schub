package pt.afonsosousah.scientificconferences

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                Toast.makeText(activity?.applicationContext, infoArrayJSON.toString(), Toast.LENGTH_LONG).show()

                val infoObjectJSON = infoArrayJSON.getJSONObject(0)

                // Populate values
                view?.findViewById<TextView>(R.id.ConferenceNameTV)?.text = infoObjectJSON.getString("Conference Name")
                view?.findViewById<TextView>(R.id.LocationTV)?.text = infoObjectJSON.getString("Location")
                view?.findViewById<TextView>(R.id.MetroTransportTV)?.text = infoObjectJSON.getString("Metro Transport")
                view?.findViewById<TextView>(R.id.BusTransportTV)?.text = infoObjectJSON.getString("Bus Transport")
                view?.findViewById<TextView>(R.id.ContactsTV)?.text = infoObjectJSON.getString("Contacts")
                view?.findViewById<TextView>(R.id.CodeConductTV)?.text = infoObjectJSON.getString("Code of Conduct")
                view?.findViewById<TextView>(R.id.StartDateTV)?.text = infoObjectJSON.getString("Start Date")
                view?.findViewById<TextView>(R.id.EndDateTV)?.text = infoObjectJSON.getString("End Date")


             /*   for (i in 0 until sessionArrayJSON.length()) {
                    val sessionJSON = sessionArrayJSON.getJSONObject(i)
                    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                    val id = sessionJSON.getInt("id")
                    val title = sessionJSON.getString("title")
                    val datetime_start = LocalDateTime.parse(sessionJSON.getString("datetime_start"), datetimeFormatter)
                    val datetime_end = LocalDateTime.parse(sessionJSON.getString("datetime_end"), datetimeFormatter)
                    val room_name = sessionJSON.getString("room_name")
                    sessionList.add(Session(id, title, datetime_start, datetime_end, room_name))
                }

                sessionList = ArrayList(sessionList.sortedBy { it.datetime_start })

                val nextSessionCardView = view?.findViewById<CardView>(R.id.nextSessionCardView)
                val nextSessionTitleTV = view?.findViewById<TextView>(R.id.nextSessionTitleTV)
                val nextSessionRoomTV = view?.findViewById<TextView>(R.id.nextSessionRoomTV)
                val nextSessionInTV = view?.findViewById<TextView>(R.id.nextSessionInTV)

                val nextSession = sessionList.find { Duration.between(LocalDateTime.now(), it.datetime_start) > Duration.ZERO }
                if (nextSession !== null) {
                    nextSessionTitleTV?.text = nextSession?.title
                    nextSessionRoomTV?.text = nextSession?.room_name
                    val duration = Duration.between(LocalDateTime.now(), nextSession?.datetime_start)
                    nextSessionInTV?.text = "Next session in ${duration.toDaysPart().toString()}d${duration.toHoursPart().toString()}h${duration.toMinutesPart()}m"
                } else {
                    nextSessionCardView?.visibility = View.GONE
                    nextSessionInTV?.text = "There are no sessions in the near future"
               } */
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

