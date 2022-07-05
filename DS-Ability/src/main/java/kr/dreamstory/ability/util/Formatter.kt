package kr.dreamstory.ability.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

private val format = DecimalFormat("#,##0")
private val doubleFormat = DecimalFormat("0.0")
private val doubleFormatScale2 = DecimalFormat("0.0#")
private val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 ( kk:mm:ss )")

fun Int.integerFormat(): String = format.format(this)

fun Long.longFormat(): String = format.format(this)

fun Double.doubleFormat(): String = doubleFormat.format(this)

fun Double.doubleFormatScale2(): String = doubleFormatScale2.format(this)

fun Date.format(): String = dateFormat.format(this)