package pt.afonsosousah.scientificconferences

import java.time.LocalDateTime

data class Session(
    var id: Int = 0,
    var title: String,
    var datetime_start: LocalDateTime,
    var datetime_end: LocalDateTime,
    var room_name: String
)
