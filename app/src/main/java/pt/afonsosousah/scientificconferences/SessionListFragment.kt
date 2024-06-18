package pt.afonsosousah.scientificconferences

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SessionListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SessionListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Create empty session list
        var sessionList = ArrayList<Session>()

        // Get the sessions from API
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = getString(R.string.endpoint) + "/api"
        val sessionsJsonRequest = JSONObject()
        sessionsJsonRequest.put("action", "getEntries")
        sessionsJsonRequest.put("table", "sessions")
        val sessionsJsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,
            sessionsJsonRequest,
            { response ->
                //Toast.makeText(activity?.applicationContext, response.toString(), Toast.LENGTH_LONG).show()
                //showDialog(requireActivity(), response.toString())

                val sessionArrayJSON = response.getJSONArray("response")

                for (i in 0 until sessionArrayJSON.length()) {
                    val sessionJSON = sessionArrayJSON.getJSONObject(i)
                    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                    val id = sessionJSON.getInt("id")
                    val title = sessionJSON.getString("title")
                    val datetime_start = LocalDateTime.parse(sessionJSON.getString("datetime_start"), datetimeFormatter)
                    val datetime_end = LocalDateTime.parse(sessionJSON.getString("datetime_end"), datetimeFormatter)
                    val room_name = sessionJSON.getString("room_name")
                    sessionList.add(Session(id, title, datetime_start, datetime_end, room_name))
                }

                // Get recycler view, create adapter and populate it
                val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext)
                val adapter = SessionListAdapter(sessionList, object :
                    SessionListAdapter.OnItemClickListener {
                    override fun onItemClick(session: Session) {
                        //Toast.makeText(activity?.applicationContext, session.id.toString(), Toast.LENGTH_SHORT).show()

                        val datetimeFormatterHours = DateTimeFormatter.ofPattern("HH:mm")
                        val datetimeFormatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val hours = "${session.datetime_start.format(datetimeFormatterHours)} - ${session.datetime_end.format(datetimeFormatterHours)}"
                        val day = "${session.datetime_start.format(datetimeFormatterDay)}"

                        showDialog(requireActivity(),
                            "Session ${session.id}\n" +
                                    "${session.title}\n" +
                                    "${hours}\n" +
                                    "${day}")
                    }
                })
                recyclerView?.adapter = adapter
            },
            { error ->
                Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(sessionsJsonObjectRequest)
    }

    private fun showDialog(context: Context?, message: String) {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.apply {
            setMessage(message)
        }.create().show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SessionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SessionListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}