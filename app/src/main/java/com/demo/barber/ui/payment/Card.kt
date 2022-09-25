package com.demo.barber.ui.payment

data class Card(val number: String, val month: String, val year: String, val pin: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

        if (number != other.number) return false
        if (month != other.month) return false
        if (year != other.year) return false
        if (pin != other.pin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number.hashCode()
        result = 31 * result + month.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + pin.hashCode()
        return result
    }
}