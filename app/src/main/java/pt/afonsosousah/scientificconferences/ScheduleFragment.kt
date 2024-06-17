package pt.afonsosousah.scientificconferences

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
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
    }

    private fun showDialog(context: Context?, message: String) {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.apply {
            setMessage(message)
        }.create().show()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_schedule, container, false)

        // Create empty article list
        var dayList = ArrayList<ScheduleDay>()
        val sessionList = ArrayList<ScheduleSession>()
        val articleList = ArrayList<ScheduleArticle>()

        // Get the articles from API
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = "http://10.0.2.2/schub/api.php"
        val scheduleJsonRequest = JSONObject()
        scheduleJsonRequest.put("action", "getSchedule")
        val scheduleJsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,
            scheduleJsonRequest,
            { response ->

                val scheduleArrayJSON = response.getJSONArray("response")

                for (i in 0 until scheduleArrayJSON.length()) {
                    val scheduleJSON = scheduleArrayJSON.getJSONObject(i)

                    val day = scheduleJSON.getString("day")

                    // Get all sessions to ArrayList<ScheduleSession>
                    val sessionsArrayJSON = scheduleJSON.getJSONArray("sessions")
                    for (i in 0 until sessionsArrayJSON.length()) {
                        val sessionJSON = sessionsArrayJSON.getJSONObject(i)

                        val title = sessionJSON.getString("title")
                        val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val datetimeFormatterHours = DateTimeFormatter.ofPattern("HH:mm")
                        val datetime_start = LocalDateTime.parse(sessionJSON.getString("datetime_start"), datetimeFormatter)
                        val datetime_end = LocalDateTime.parse(sessionJSON.getString("datetime_end"), datetimeFormatter)
                        val hours = "${datetime_start.format(datetimeFormatterHours)} - ${datetime_end.format(datetimeFormatterHours)}"
                        //val hours = sessionJSON.getString("hours")
                        val room = sessionJSON.getString("room_name")

                        // Get all articles to ArrayList<ScheduleArticles>
                        val articleArrayJSON = sessionJSON.getJSONArray("articles")
                        for(i in 0 until articleArrayJSON.length()) {
                            val articleJSON = articleArrayJSON.getJSONObject(i)

                            val title = articleJSON.getString("title")
                            val authors = articleJSON.getString("authors")

                            articleList.add(ScheduleArticle(title, authors))
                        }

                        sessionList.add(ScheduleSession(title, hours, room, articleList))
                    }

                    dayList.add(ScheduleDay(day, sessionList))
                }

                // Get recycler view, create adapter and populate it
                val daysRecyclerView = rootView.findViewById<RecyclerView>(R.id.daysRecyclerView)
                daysRecyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext)
                val daysAdapter = ScheduleDayListAdapter(dayList, object :
                    ScheduleDayListAdapter.OnItemClickListener {
                    override fun onItemClick(day: ScheduleDay) {
                        Toast.makeText(activity?.applicationContext, day.sessions[0].title, Toast.LENGTH_SHORT).show()
                    }
                })
                daysRecyclerView?.adapter = daysAdapter
            },
            { error ->
                showDialog(requireActivity(), error.toString())
                //Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(scheduleJsonObjectRequest)

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}