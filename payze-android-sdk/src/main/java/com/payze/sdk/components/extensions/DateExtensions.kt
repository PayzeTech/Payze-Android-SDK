package com.payze.sdk.components.extensions

import java.util.Calendar
import java.util.TimeZone

fun Long.getYear(timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Int {
    val cal = Calendar.getInstance(timeZone)
    cal.timeInMillis = this
    return cal.get(Calendar.YEAR)
}

fun Long.getMonth(timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Int {
    val cal = Calendar.getInstance(timeZone)
    cal.timeInMillis = this
    return cal.get(Calendar.MONTH)
}

fun String.validDate(): Boolean {
    val currentDate = System.currentTimeMillis()

    val currentYear = currentDate.getYear().toString().takeLast(2).toInt()
    val currentMonth = currentDate.getMonth() + 1

    val nextFiveYears = (currentDate.getYear() + 5).toString().takeLast(2).toInt()

    if (this.length < 7)
        return false

    val cardYear = this.takeLast(2).toInt()
    val cardMonth = this.take(2).toInt()

    if (cardMonth > 12)
        return false

    if (cardYear < currentYear)
        return false

    if (cardYear > nextFiveYears)
        return false

    if (cardYear == currentYear) {
        return cardMonth >= currentMonth
    }

    return true
}