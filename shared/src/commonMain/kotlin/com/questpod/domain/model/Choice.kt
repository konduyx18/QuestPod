package com.questpod.domain.model

import java.util.UUID
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Represents a choice in a story node that leads to another node.
 */
@Parcelize
data class Choice(
    val text: String,
    val leadsTo: UUID
) : Parcelable {
    override fun toString(): String {
        return "Choice(text='$text', leadsTo=$leadsTo)"
    }
}
