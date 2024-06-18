package pt.afonsosousah.scientificconferences

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ArticleDetailFragment : Fragment() {

    private var id: Int? = null
    private var title: String? = null
    private var authors: String? = null
    private var date_published: LocalDate? = null
    private var abstract: String? = null
    private var pdf: String? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt("id")
            title = it.getString("title")
            authors = it.getString("authors")
            date_published = it.getSerializable("date_published") as LocalDate
            abstract = it.getString("abstract")
            pdf = it.getString("pdf")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_article_detail, container, false)

        val titleTV = rootView.findViewById<TextView>(R.id.titleTV)
        val authorsTV = rootView.findViewById<TextView>(R.id.authorsTV)
        val dateTV = rootView.findViewById<TextView>(R.id.dateTV)
        val abstractTV = rootView.findViewById<TextView>(R.id.abstractTV)

        titleTV?.text = title
        authorsTV?.text = authors
        dateTV?.text = date_published.toString()
        abstractTV?.text = abstract


        // Add button click listeners
        val backButton = rootView.findViewById<AppCompatButton>(R.id.backButton)
        backButton.setOnClickListener {
            replaceFragment(ArticleListFragment())
        }
        val downloadPDFButton = rootView.findViewById<AppCompatButton>(R.id.downloadPDFButton)
        downloadPDFButton.setOnClickListener {
            val url = getString(R.string.endpoint) + "/articles/$pdf";
            val request = InputStreamVolleyRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        if (response!=null) {

                            val fileName = "$pdf";
                            val content = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                            }
                            val uri = rootView.context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, content)
                            uri?.let {
                                rootView.context.contentResolver.openOutputStream(uri)?.apply {
                                    BufferedOutputStream(this).write(response)
                                }

                                Toast.makeText(rootView.context, "Download completed", Toast.LENGTH_LONG).show()

                                // Open file in external PDF viewer
                                val target = Intent(Intent.ACTION_VIEW)
                                target.setDataAndType(uri, "application/pdf")
                                target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                val intent = Intent.createChooser(target, "Open File")
                                try {
                                    startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(rootView.context, "No valid PDF file viewer found", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(rootView.context, "Unable to download file", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                },
                { error ->
                    Toast.makeText(rootView.context, "Error while downloading file $error", Toast.LENGTH_LONG).show();
                }
            )
            val queue = Volley.newRequestQueue(rootView.context);
            queue.add(request);
        }
        val sendQuestionButton = rootView.findViewById<AppCompatButton>(R.id.sendQuestionButton)
        sendQuestionButton.setOnClickListener {
            val queue = Volley.newRequestQueue(activity?.applicationContext)
            val url = getString(R.string.endpoint) + "/api"
            val sendQuestionJsonRequest = JSONObject()
            val sharedPref = rootView.context.getSharedPreferences("User", Context.MODE_PRIVATE)
            val questionContentEditText = rootView.findViewById<EditText>(R.id.questionContentEditText)
            sendQuestionJsonRequest.put("action", "sendQuestion")
            sendQuestionJsonRequest.put("user_id", sharedPref.getInt("user_id", 0))
            sendQuestionJsonRequest.put("article_id", id)
            sendQuestionJsonRequest.put("content", questionContentEditText.text)
            val sendQuestionJsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url,
                sendQuestionJsonRequest,
                { response ->
                    Toast.makeText(activity?.applicationContext, response.getString("message"), Toast.LENGTH_LONG).show()
                    questionContentEditText.text = null;
                },
                { error ->
                    Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                }
            )
            queue.add(sendQuestionJsonObjectRequest)
        }


        // Create empty question list
        var questionList = ArrayList<Question>()

        // Get the questions from API
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = getString(R.string.endpoint) + "/api"
        val questionsJsonRequest = JSONObject()
        questionsJsonRequest.put("action", "getQuestions")
        questionsJsonRequest.put("article_id", id)
        val questionTitleTV = rootView.findViewById<TextView>(R.id.questionsTitleTV)
        val questionsJsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,
            questionsJsonRequest,
            { response ->

                val message = response.optString("message")

                if (message.isNullOrBlank()) {
                    val questionArrayJSON = response.getJSONArray("response")

                    if (questionArrayJSON.length() == 0) questionTitleTV.text = "No questions yet"

                    for (i in 0 until questionArrayJSON.length()) {
                        val questionJSON = questionArrayJSON.getJSONObject(i)

                        val id = questionJSON.getInt("id")
                        val user_id = questionJSON.getInt("user_id")
                        val username = questionJSON.getString("username")
                        val article_id = questionJSON.getInt("article_id")
                        val content = questionJSON.getString("content")
                        val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val datetime = LocalDateTime.parse(questionJSON.getString("datetime"), datetimeFormatter)
                        questionList.add(Question(id, user_id, username, article_id, content, datetime))
                    }
                } else {
                    Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_SHORT).show()
                    //questionList.add(Question(0, 0, "", 0, "Error while"))
                    questionTitleTV.text = "Error fetching questions"
                }

                // Get recycler view, create adapter and populate it
                val recyclerView = view?.findViewById<RecyclerView>(R.id.questionsRecyclerView)
                recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext)
                val adapter = QuestionListAdapter(questionList, object :
                    QuestionListAdapter.OnItemClickListener {
                    override fun onItemClick(question: Question) {
                        //Toast.makeText(activity?.applicationContext, article.id.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
                recyclerView?.adapter = adapter

            },
            { error ->
                Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(questionsJsonObjectRequest)

        return rootView
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ArticleDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArticleDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    */
}