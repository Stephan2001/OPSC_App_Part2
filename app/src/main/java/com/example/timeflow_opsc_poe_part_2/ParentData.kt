package com.example.timeflow_opsc_poe_part_2

data class ParentData(
    val parentTitle:String?=null,
    var type:Int = Constants.PARENT,
    var subList : MutableList<ChildData> = ArrayList(),
    var isExpanded:Boolean = false
)