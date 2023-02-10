package org.saphka.discord.bot.model

data class GameEnrollmentDTO(
    val id: Long,
    val serverId: Long,
    val gameId: Long,
    val characterId: Long
)

data class GameEnrollmentChangeRequest(
    val serverId: Long,
    val gameSlug: String,
    val characterSlug: String
)