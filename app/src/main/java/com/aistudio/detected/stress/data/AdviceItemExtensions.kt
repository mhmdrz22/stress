package com.aistudio.detected.stress.data

fun AdviceItem.supports(level: StressLevel): Boolean {
    val levelOrd = level.ordinal
    return levelOrd >= this.minLevel.ordinal && levelOrd <= this.maxLevel.ordinal
}
