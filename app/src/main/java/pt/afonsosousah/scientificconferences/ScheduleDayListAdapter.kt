package pt.afonsosousah.scientificconferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ScheduleDayListAdapter(private var dayList: List<ScheduleDay>, private val
listener: OnItemClickListener) :
    RecyclerView.Adapter<ScheduleDayListAdapter.DayViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(day: ScheduleDay)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_day_card, parent, false)
        return DayViewHolder(view)
    }
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = dayList[position]
        holder.bind(day)
    }
    override fun getItemCount(): Int = dayList.size
    inner class DayViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val dayTV: TextView = itemView.findViewById(R.id.dayTV)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(dayList[position])
            }
        }
        fun bind(day: ScheduleDay) {
            dayTV.text = day.dayString

            val sessionsRecyclerView = itemView.findViewById<RecyclerView>(R.id.sessionsRecyclerView)
            sessionsRecyclerView?.layoutManager = LinearLayoutManager(itemView.context)
            val sessionsAdapter = ScheduleSessionListAdapter(day.sessions, object :
                ScheduleSessionListAdapter.OnItemClickListener {
                override fun onItemClick(session: ScheduleSession) {
                    Toast.makeText(itemView.context, session.title.toString(), Toast.LENGTH_SHORT).show()
                }
            })
            sessionsRecyclerView?.adapter = sessionsAdapter
        }
    }
}