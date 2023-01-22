package org.saphka.discord.bot.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
class Character(
    @Id
    var id: Long?,
    var serverId: Long,
    var ownerId: Long,
    var slug: String,
    var name: String,
    var sheet: String,
    var avatar: String
)