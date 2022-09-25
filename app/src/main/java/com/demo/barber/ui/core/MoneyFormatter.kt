package com.demo.barber.ui.core

fun Double.toMoney() = "$this KWD"
fun String.fromMoney() = this.replace("KWD", "").trim().toDouble()