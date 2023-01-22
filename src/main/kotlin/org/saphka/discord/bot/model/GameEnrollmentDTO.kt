package org.saphka.discord.bot.model

class GameEnrollmentDTO(
    val id: Long? = null,
    val serverId: Long,
    val gameId: Long,
    val characterId: Long
)