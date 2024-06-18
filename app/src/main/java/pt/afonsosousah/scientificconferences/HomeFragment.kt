package pt.afonsosousah.scientificconferences

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @RequiresApi(Build.VERSION_CODES.S)
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
        val url = getString(R.string.endpoint) + "/api";
        val sessionsJsonRequest = JSONObject()
        sessionsJsonRequest.put("action", "getEntries")
        sessionsJsonRequest.put("table", "sessions")
        val sessionsJsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,
            sessionsJsonRequest,
            { response ->
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
                    nextSessionInTV?.text = "Next session in ${duration.toDaysPart().toString()} days ${duration.toHoursPart().toString()} hours ${duration.toMinutesPart()} minutes:"
                } else {
                    nextSessionCardView?.visibility = View.GONE
                    nextSessionInTV?.text = "There are no sessions in the near future"
                }
            },
            { error ->
                Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(sessionsJsonObjectRequest)

        // Create empty article list
        var articleList = ArrayList<Article>()

        // Get the articles from API
        val articlesJsonRequest = JSONObject()
        articlesJsonRequest.put("action", "getEntries")
        articlesJsonRequest.put("table", "articles")
        val articlesJsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,
            articlesJsonRequest,
            { response ->

                val articleArrayJSON = response.getJSONArray("response")

                for (i in 0 until articleArrayJSON.length()) {
                    val articleJSON = articleArrayJSON.getJSONObject(i)

                    val id = articleJSON.getInt("id")
                    val title = articleJSON.getString("title")
                    val date_published = LocalDate.parse(articleJSON.getString("date_published"))
                    val session_id = articleJSON.getInt("session_id")
                    val abstract = articleJSON.getString("abstract")
                    val pdf = articleJSON.getString("pdf")
                    val authorList = articleJSON.getString("authors")
                    articleList.add(Article(id, title, date_published, session_id, abstract, pdf, authorList))
                }

                val featuredArticles = articleList.shuffled().take(2)
                for (i in featuredArticles.indices) {
                    val article = featuredArticles[i]
                    val context = requireActivity().applicationContext
                    val titleTVid = context.resources.getIdentifier("featuredArticle${i+1}TitleTV", "id", context.packageName)
                    val authorsTVid = context.resources.getIdentifier("featuredArticle${i+1}AuthorsTV", "id", context.packageName)
                    val titleTV = view?.findViewById<TextView>(titleTVid)
                    val authorsTV = view?.findViewById<TextView>(authorsTVid)

                    titleTV?.text = article.title
                    authorsTV?.text = article.authors
                }
            },
            { error ->
                Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(articlesJsonObjectRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        val infoButton = rootView.findViewById<AppCompatButton>(R.id.infoButton)
        infoButton.setOnClickListener {
            replaceFragment(InfoFragment())
        }
        val requestInfoButton = rootView.findViewById<AppCompatButton>(R.id.requestInfoButton)
        requestInfoButton.setOnClickListener {
            replaceFragment(ContactFormFragment())
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}