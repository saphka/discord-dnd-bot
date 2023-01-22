package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.domain.GameLog
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.model.GameLogDTO
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class GameLogMapper {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun toDto(it: GameLog) = GameLogDTO(
        id = it.id,
        serverId = it.serverId,
        gameId = it.gameId,
        characterId = it.characterId,
        createdAt = it.createdAt,
        entryText = it.entryText
    )

    fun toEntity(it: GameLogDTO) = GameLog(
        id = it.id,
        serverId = it.serverId,
        gameId = it.gameId,
        characterId = it.characterId,
        createdAt = it.createdAt,
        entryText = it.entryText
    )

    fun toMessage(it: GameLogDTO, character: CharacterDTO?) =
        "${formatter.format(it.createdAt)} - **${character?.name}** - ${it.entryText}"

    fun fromEvent(event: ChatInputInteractionEvent, game: GameDTO, character: CharacterDTO): GameLogDTO {
        val options = event.options.first()
        return GameLogDTO(
            serverId = event.interaction.guildId.orElseThrow().asLong(),
            gameId = game.id!!,
            characterId = character.id!!,
            createdAt = LocalDateTime.now(),
            entryText = options.getOption("text").flatMap { it.value }.map { it.asString() }.orElse("")
        )
    }

}