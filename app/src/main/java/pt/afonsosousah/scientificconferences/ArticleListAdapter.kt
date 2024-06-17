package pt.afonsosousah.scientificconferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArticleListAdapter(private var articleList: List<Article>, private val
listener: OnItemClickListener) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.article_card, parent, false)
        return ArticleViewHolder(view)
    }
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articleList[position]
        holder.bind(article)
    }
    override fun getItemCount(): Int = articleList.size
    inner class ArticleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        val authorsTV: TextView = itemView.findViewById(R.id.authorsTV)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(articleList[position])
            }
        }
        fun bind(article: Article) {
            titleTV.text = article.title
            authorsTV.text = article.authors.toString()
        }
    }

    fun setFilteredList(articleList: List<Article>) {
        this.articleList = articleList
        notifyDataSetChanged()
    }
}