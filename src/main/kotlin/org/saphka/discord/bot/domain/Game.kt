package org.saphka.discord.bot.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table
class Game(
    @Id
    var id: Long?,
    var serverId: Long,
    var slug: String,
    var name: String,
    var startsAt: LocalDateTime,
    var tier: String
)