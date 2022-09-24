package com.demo.saloni.ui.core

fun Double.toMoney() = "$this KWD"
fun String.fromMoney() = this.replace("KWD", "").trim().toDouble()