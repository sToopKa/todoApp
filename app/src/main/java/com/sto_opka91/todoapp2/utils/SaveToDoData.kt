package com.sto_opka91.todoapp2.utils

data class SaveToDoData(val toDo: String, val date: String = ""){
    constructor() : this("", "")
}
