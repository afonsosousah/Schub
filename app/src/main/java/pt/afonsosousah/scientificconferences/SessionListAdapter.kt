package pt.afonsosousah.scientificconferences

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter

class SessionListAdapter(private var sessionList: List<Session>, private val
listener: OnItemClickListener) :
    RecyclerView.Adapter<SessionListAdapter.SessionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(session: Session)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.session_card, parent, false)
        return SessionViewHolder(view)
    }
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionList[position]
        holder.bind(session)
    }
    override fun getItemCount(): Int = sessionList.size
    inner class SessionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        val timeTV: TextView = itemView.findViewById(R.id.timeTV)
        val dateTV: TextView = itemView.findViewById(R.id.dateTV)
        val roomTV: TextView = itemView.findViewById(R.id.roomTV)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(sessionList[position])
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(session: Session) {
            val datetimeFormatterHours = DateTimeFormatter.ofPattern("HH:mm")
            val datetimeFormatterDay = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            titleTV.text = session.title
            timeTV.text = "${session.datetime_start.format(datetimeFormatterHours)} - ${session.datetime_end.format(datetimeFormatterHours)}"
            dateTV.text = "${session.datetime_start.format(datetimeFormatterDay)}"
            roomTV.text = session.room_name
        }
    }

    fun setFilteredList(sessionList: List<Session>) {
        this.sessionList = sessionList
        notifyDataSetChanged()
    }
}