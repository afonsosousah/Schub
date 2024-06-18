package pt.afonsosousah.scientificconferences

data class ScheduleDay(
    var dayString: String,
    var sessions: ArrayList<ScheduleSession>
)
