package com.example.smartpick.core.utils

import com.example.smartpick.core.model.ReactionType

object ReactionUtils {
    fun updateReactionState(
        targetReaction: ReactionType,
        currentReaction: ReactionType?,
        currentCount: Int,
        currentBreakdown: Map<ReactionType, Int>
    ): Triple<ReactionType?, Int, Map<ReactionType, Int>> {
        val newBreakdown = currentBreakdown.toMutableMap()
        var newReaction = currentReaction
        var newCount = currentCount

        if (currentReaction == targetReaction) {
            newReaction = null
            newCount = maxOf(0, currentCount - 1)
            newBreakdown[targetReaction] = maxOf(0, (newBreakdown[targetReaction] ?: 0) - 1)
            if (newBreakdown[targetReaction] == 0) newBreakdown.remove(targetReaction)
        } else {
            if (currentReaction != null) {
                newBreakdown[currentReaction] = maxOf(0, (newBreakdown[currentReaction] ?: 0) - 1)
                if (newBreakdown[currentReaction] == 0) newBreakdown.remove(currentReaction)
            } else {
                newCount += 1
            }
            newReaction = targetReaction
            newBreakdown[targetReaction] = (newBreakdown[targetReaction] ?: 0) + 1
        }
        return Triple(newReaction, newCount, newBreakdown)
    }
}