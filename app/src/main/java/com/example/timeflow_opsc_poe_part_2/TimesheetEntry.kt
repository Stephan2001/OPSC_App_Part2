package com.example.timeflow_opsc_poe_part_2

data class TimesheetEntry(
    var date : String = "",
    var project: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var desc: String = "",
    var photoReference: String= ""
)