package pt.afonsosousah.scientificconferences

import java.time.LocalDate

data class Article(
    var id: Int = 0,
    var title: String,
    var date_published: LocalDate,
    var session_id: Int,
    var abstract: String,
    var pdf: String,
    var authors: String
)
