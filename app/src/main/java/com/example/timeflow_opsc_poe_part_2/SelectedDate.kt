package com.example.timeflow_opsc_poe_part_2

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SelectedDate {
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
    var date: String = formatter.format(Date())
}