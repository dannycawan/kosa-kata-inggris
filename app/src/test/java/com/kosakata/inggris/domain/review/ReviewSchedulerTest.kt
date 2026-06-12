package com.kosakata.inggris.domain.review

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class ReviewSchedulerTest {
    private val now = 1_000_000L

    @Test
    fun correctAnswersUseExpectedIntervals() {
        assertEquals(now + TimeUnit.DAYS.toMillis(1), ReviewScheduler.nextReviewAt(1, now))
        assertEquals(now + TimeUnit.DAYS.toMillis(3), ReviewScheduler.nextReviewAt(2, now))
        assertEquals(now + TimeUnit.DAYS.toMillis(7), ReviewScheduler.nextReviewAt(3, now))
        assertEquals(now + TimeUnit.DAYS.toMillis(14), ReviewScheduler.nextReviewAt(4, now))
        assertNull(ReviewScheduler.nextReviewAt(5, now))
    }

    @Test
    fun wrongAnswerReturnsTomorrow() {
        assertEquals(now + TimeUnit.DAYS.toMillis(1), ReviewScheduler.tomorrow(now))
    }
}
