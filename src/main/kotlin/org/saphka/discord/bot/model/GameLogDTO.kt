package org.saphka.discord.bot.model

import java.time.LocalDateTime

class GameLogDTO(
    val id: Long? = null,
    val serverId: Long,
    val gameId: Long,
    val characterId: Long,
    val createdAt: LocalDateTime,
    val entryText: String
)