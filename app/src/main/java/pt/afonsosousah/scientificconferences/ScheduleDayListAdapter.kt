package pt.afonsosousah.scientificconferences

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter


class ScheduleDayListAdapter(private var dayList: List<ScheduleDay>, private val
listener: OnItemClickListener?
) :
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
    @RequiresApi(Build.VERSION_CODES.O)
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
                listener?.onItemClick(dayList[position])
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(day: ScheduleDay) {
            val dateFormatter = DateTimeFormatter.ofPattern("dd LLL")
            dayTV.text = day.date.format(dateFormatter)

            val sessionsRecyclerView = itemView.findViewById<RecyclerView>(R.id.sessionsRecyclerView)
            sessionsRecyclerView?.layoutManager = LinearLayoutManager(itemView.context)
            val sessionsAdapter = ScheduleSessionListAdapter(day.sessions, null)
            sessionsRecyclerView?.adapter = sessionsAdapter
        }
    }
}