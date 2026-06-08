package com.example.smartpick.core.utils

import com.example.smartpick.BaseUnitTest
import com.example.smartpick.core.model.ReactionType
import org.junit.Assert.*
import org.junit.Test

class ReactionUtilsTest : BaseUnitTest() {

    @Test
    fun `updateReactionState - Them reaction moi khi dang null`() {
        val breakdown = emptyMap<ReactionType, Int>()
        val result = ReactionUtils.updateReactionState(
            targetReaction = ReactionType.LIKE,
            currentReaction = null,
            currentCount = 0,
            currentBreakdown = breakdown
        )

        // Triple(newReaction, newCount, newBreakdown)
        assertEquals(ReactionType.LIKE, result.first)
        assertEquals(1, result.second)
        assertEquals(1, result.third[ReactionType.LIKE])
    }

    @Test
    fun `updateReactionState - Huy reaction khi nhap trung target`() {
        val breakdown = mapOf(ReactionType.LIKE to 1)
        val result = ReactionUtils.updateReactionState(
            targetReaction = ReactionType.LIKE,
            currentReaction = ReactionType.LIKE,
            currentCount = 1,
            currentBreakdown = breakdown
        )

        assertNull(result.first)
        assertEquals(0, result.second)
        assertFalse(result.third.containsKey(ReactionType.LIKE))
    }

    @Test
    fun `updateReactionState - Thay doi tu LIKE sang LOVE`() {
        val breakdown = mapOf(ReactionType.LIKE to 1)
        val result = ReactionUtils.updateReactionState(
            targetReaction = ReactionType.LOVE,
            currentReaction = ReactionType.LIKE,
            currentCount = 1,
            currentBreakdown = breakdown
        )

        assertEquals(ReactionType.LOVE, result.first)
        assertEquals(1, result.second) // Count remains 1 because we just changed reaction type
        assertFalse(result.third.containsKey(ReactionType.LIKE))
        assertEquals(1, result.third[ReactionType.LOVE])
    }

    @Test
    fun `updateReactionState - Thay doi khi co nhieu nguoi tha`() {
        val breakdown = mapOf(
            ReactionType.LIKE to 2,
            ReactionType.LOVE to 1
        )
        val result = ReactionUtils.updateReactionState(
            targetReaction = ReactionType.LOVE,
            currentReaction = ReactionType.LIKE,
            currentCount = 3,
            currentBreakdown = breakdown
        )

        assertEquals(ReactionType.LOVE, result.first)
        assertEquals(3, result.second)
        assertEquals(1, result.third[ReactionType.LIKE])
        assertEquals(2, result.third[ReactionType.LOVE])
    }
}
