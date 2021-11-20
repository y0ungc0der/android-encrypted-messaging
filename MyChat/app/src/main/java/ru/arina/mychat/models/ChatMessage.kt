package ru.arina.mychat.models

class ChatMessage(
    val id: String,
    val text: String,
    val fromID: String,
    val toID: String,
    val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}