package org.saphka.discord.bot.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
class GameEnrollment(
    @Id
    var id: Long?,
    var serverId: Long,
    var gameId: Long,
    var characterId: Long
)