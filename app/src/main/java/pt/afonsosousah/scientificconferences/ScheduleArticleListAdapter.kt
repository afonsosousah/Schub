package pt.afonsosousah.scientificconferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleArticleListAdapter(private var articleList: List<ScheduleArticle>, private val
listener: OnItemClickListener) :
    RecyclerView.Adapter<ScheduleArticleListAdapter.ArticleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(article: ScheduleArticle)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_article_card, parent, false)
        return ArticleViewHolder(view)
    }
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articleList[position]
        holder.bind(article)
    }
    override fun getItemCount(): Int = articleList.size
    inner class ArticleViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleTV: TextView = itemView.findViewById(R.id.articleTitleTV)
        val authorsTV: TextView = itemView.findViewById(R.id.articleAuthorsTV)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(articleList[position])
            }
        }
        fun bind(article: ScheduleArticle) {
            titleTV.text = article.title
            authorsTV.text = article.authors
        }
    }
}