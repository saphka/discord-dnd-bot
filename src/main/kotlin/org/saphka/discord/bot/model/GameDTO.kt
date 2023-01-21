package org.saphka.discord.bot.model

import java.time.LocalDateTime

data class GameDTO(
    val id: Long? = null,
    val serverId: Long,
    val slug: String,
    val name: String,
    val startsAt: LocalDateTime,
    val tier: String
)
