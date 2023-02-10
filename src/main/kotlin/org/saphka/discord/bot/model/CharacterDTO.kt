package org.saphka.discord.bot.model

data class CharacterDTO(
    val id: Long,
    val serverId: Long,
    val ownerId: Long,
    val slug: String,
    val name: String,
    val sheet: String,
    val avatar: String
)

data class CharacterCreateRequest(
    val serverId: Long,
    val ownerId: Long,
    val slug: String,
    val name: String,
    val sheet: String,
    val avatar: String
)