package org.saphka.discord.bot.model

import java.time.LocalDateTime

data class GameLogDTO(
    val id: Long,
    val serverId: Long,
    val gameId: Long,
    val characterId: Long,
    val createdAt: LocalDateTime,
    val entryText: String
)

data class GameLogCreateRequest(
    val serverId: Long,
    val gameSlug: String,
    val characterSlug: String,
    val createdAt: LocalDateTime,
    val entryText: String
)