package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.command.FieldName
import org.saphka.discord.bot.domain.GameLog
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.model.GameLogCreateRequest
import org.saphka.discord.bot.model.GameLogDTO
import org.saphka.discord.bot.service.TimeAccessor
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

@Component
class GameLogMapper(
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val timeAccessor: TimeAccessor
) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun toDto(it: GameLog) = GameLogDTO(
        id = it.id!!,
        serverId = it.serverId,
        gameId = it.gameId,
        characterId = it.characterId,
        createdAt = it.createdAt,
        entryText = it.entryText
    )

    fun toEntity(it: GameLogCreateRequest, game: GameDTO, character: CharacterDTO) = GameLog(
        id = null,
        serverId = it.serverId,
        gameId = game.id,
        characterId = character.id,
        createdAt = it.createdAt,
        entryText = it.entryText
    )

    fun toMessage(it: GameLogDTO, character: CharacterDTO?) =
        "${formatter.format(it.createdAt)} - **${character?.name}** - ${it.entryText}"

    fun fromEvent(event: ChatInputInteractionEvent): GameLogCreateRequest {
        val options = event.options.first()
        return GameLogCreateRequest(
            serverId = eventPropertiesMapper.getServerId(event),
            gameSlug = eventPropertiesMapper.getGameSlug(event),
            characterSlug = eventPropertiesMapper.getCharacterSlug(event),
            createdAt = timeAccessor.getCurrentTime(),
            entryText = options.getOption(FieldName.GAME_LOG_TEXT).flatMap { it.value }.map { it.asString() }.orElse("")
        )
    }

}