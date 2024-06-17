package pt.afonsosousah.scientificconferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        fun bind(question: Question) {
            contentTV.text = question.content
            usernameTV.text = question.username
        }
    }
}