package pt.afonsosousah.scientificconferences

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.Duration
import java.time.LocalDateTime

class QuestionListAdapter(private var questionList: List<Question>, private val
listener: OnItemClickListener) :
    RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(question: Question)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_card, parent, false)
        return QuestionViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questionList[position]
        holder.bind(question)
    }
    override fun getItemCount(): Int = questionList.size
    inner class QuestionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val contentTV: TextView = itemView.findViewById(R.id.questionContentTV)
        val usernameTV: TextView = itemView.findViewById(R.id.questionUsernameTV)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(questionList[position])
            }
        }
        @RequiresApi(Build.VERSION_CODES.S)
        fun bind(question: Question) {
            contentTV.text = question.content
            var timeString = "";
            var durationSince = Duration.between(question.dateTime, LocalDateTime.now())
            val days = durationSince.toDaysPart().toInt()
            if (days == 0) timeString = "today"
            else if (days == 1) timeString = " yesterday"
            else if (days > 1) timeString = "${days} ago"
            usernameTV.text = "by '${question.username}' ${timeString}"
        }
    }
}