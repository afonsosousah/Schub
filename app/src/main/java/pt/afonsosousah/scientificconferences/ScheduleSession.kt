package pt.afonsosousah.scientificconferences

data class ScheduleSession(
    var title: String,
    var hours: String,
    var room: String,
    var articles: ArrayList<Article>
)
