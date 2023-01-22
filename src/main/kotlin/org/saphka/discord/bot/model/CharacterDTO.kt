package org.saphka.discord.bot.model

data class CharacterDTO(
    val id: Long? = null,
    val serverId: Long,
    val ownerId: Long,
    val slug: String,
    val name: String,
    val sheet: String,
    val avatar: String
)