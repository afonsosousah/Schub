package pt.afonsosousah.scientificconferences

import java.time.LocalDate

data class ScheduleDay(
    var date: LocalDate,
    var sessions: ArrayList<ScheduleSession>
)
