package org.saphka.discord.bot.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table
class GameLog(
    @Id
    var id: Long?,
    var serverId: Long,
    var gameId: Long,
    var characterId: Long,
    var createdAt: LocalDateTime,
    var entryText: String
)