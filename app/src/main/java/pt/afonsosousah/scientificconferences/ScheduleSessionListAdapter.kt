package pt.afonsosousah.scientificconferences

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScheduleSessionListAdapter(private var sessionList: List<ScheduleSession>, private val
listener: OnItemClickListener?
) :
    RecyclerView.Adapter<ScheduleSessionListAdapter.ScheduleSessionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(session: ScheduleSession)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ScheduleSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_session_card, parent, false)
        return ScheduleSessionViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ScheduleSessionViewHolder, position: Int) {
        val session = sessionList[position]
        holder.bind(session)
    }
    override fun getItemCount(): Int = sessionList.size
    inner class ScheduleSessionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleTV: TextView = itemView.findViewById(R.id.sessionTitleTV)
        val timeTV: TextView = itemView.findViewById(R.id.sessionHoursTV)
        val roomTV: TextView = itemView.findViewById(R.id.sessionRoomTV)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener?.onItemClick(sessionList[position])
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(session: ScheduleSession) {
            titleTV.text = session.title
            timeTV.text = session.hours
            roomTV.text = session.room

            val articlesRecyclerView = itemView.findViewById<RecyclerView>(R.id.articlesRecyclerView)
            articlesRecyclerView?.layoutManager = LinearLayoutManager(itemView.context)
            val articlesAdapter = ScheduleArticleListAdapter(session.articles, object :
                ScheduleArticleListAdapter.OnItemClickListener {
                override fun onItemClick(article: Article) {
                    //Toast.makeText(itemView.context, article.title.toString(), Toast.LENGTH_SHORT).show()
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
                    replaceFragment(detailFragment, itemView.findFragment<ScheduleFragment>())
                }
            })
            articlesRecyclerView?.adapter = articlesAdapter
        }

        private fun replaceFragment(fragment: Fragment, hostFragment: Fragment) {
            hostFragment.parentFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
        }
    }
}