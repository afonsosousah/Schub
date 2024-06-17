package pt.afonsosousah.scientificconferences

import java.time.LocalDate

data class Question(
    var id: Int = 0,
    var user_id: Int,
    var username: String,
    var article_id: Int,
    var content: String
)
