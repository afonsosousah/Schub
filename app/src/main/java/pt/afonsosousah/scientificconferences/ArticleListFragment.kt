package pt.afonsosousah.scientificconferences

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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.time.LocalDate
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleListFragment : Fragment() {
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

        // Create empty article list
        var articleList = ArrayList<Article>()

        // Get the articles from API
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val url = getString(R.string.endpoint) + "/api.php"
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

                // Get recycler view, create adapter and populate it
                val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext)
                val adapter = ArticleListAdapter(articleList, object :
                    ArticleListAdapter.OnItemClickListener {
                    override fun onItemClick(article: Article) {
                        //Toast.makeText(activity?.applicationContext, article.id.toString(), Toast.LENGTH_SHORT).show()
                        val detailFragment = ArticleDetailFragment()
                        val bundle = Bundle()
                        bundle.putInt("id", article.id)
                        bundle.putString("title", article.title)
                        bundle.putSerializable("date_published", article.date_published)
                        bundle.putInt("session_id", article.session_id)
                        bundle.putString("abstract", article.abstract)
                        bundle.putString("pdf", article.pdf)
                        bundle.putString("authors", article.authors)
                        detailFragment.arguments = bundle
                        replaceFragment(detailFragment)
                    }
                })
                recyclerView?.adapter = adapter

                // Search functionality
                val searchView = view?.findViewById<SearchView>(R.id.searchView)
                searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterList(newText, articleList, adapter)
                        return true
                    }
                })

            },
            { error ->
                Toast.makeText(activity?.applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(articlesJsonObjectRequest)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    private fun filterList(query: String?, articleList: ArrayList<Article>, adapter: ArticleListAdapter) {
        if (query != null) {
            val filteredList = ArrayList<Article>()
            for (article in articleList) {
                if (article.title.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT))) {
                    filteredList.add(article)
                }
            }

            if (filteredList.isEmpty()) Toast.makeText(requireActivity(), "No result", Toast.LENGTH_SHORT).show()
            adapter.setFilteredList(filteredList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ArticlesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArticleListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}