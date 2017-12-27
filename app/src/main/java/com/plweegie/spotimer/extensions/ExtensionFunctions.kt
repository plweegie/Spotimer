package com.plweegie.spotimer.extensions

fun Long.toMinutes(): Long {
    return (this / (60 * 1000))
}

fun Long.toRemainingSeconds(): Long {
    return ((this - (60 * 1000 * this.toMinutes())) / 1000)
}

fun Long.toDurationString(): String {
    return String.format("%d:%02d", this.toMinutes(), this.toRemainingSeconds())
}
