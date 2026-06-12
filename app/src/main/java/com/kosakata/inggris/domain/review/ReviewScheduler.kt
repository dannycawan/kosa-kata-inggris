package com.kosakata.inggris.domain.review

import java.util.concurrent.TimeUnit

object ReviewScheduler {
    fun nextReviewAt(correctCount: Int, now: Long = System.currentTimeMillis()): Long? {
        val days = when (correctCount) {
            0, 1 -> 1
            2 -> 3
            3 -> 7
            4 -> 14
            else -> return null
        }
        return now + TimeUnit.DAYS.toMillis(days.toLong())
    }

    fun tomorrow(now: Long = System.currentTimeMillis()): Long = now + TimeUnit.DAYS.toMillis(1)
}
