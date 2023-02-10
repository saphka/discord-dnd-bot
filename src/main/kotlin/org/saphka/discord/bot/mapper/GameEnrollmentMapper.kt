package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.domain.GameEnrollment
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.model.GameEnrollmentChangeRequest
import org.saphka.discord.bot.model.GameEnrollmentDTO
import org.springframework.stereotype.Component

@Component
class GameEnrollmentMapper(
    private val eventPropertiesMapper: EventPropertiesMapper
) {

    fun toEntity(it: GameEnrollmentChangeRequest, game: GameDTO, character: CharacterDTO) = GameEnrollment(
        id = null,
        serverId = it.serverId,
        gameId = game.id,
        characterId = character.id
    )

    fun toDto(it: GameEnrollment) = GameEnrollmentDTO(
        id = it.id!!,
        serverId = it.serverId,
        gameId = it.gameId,
        characterId = it.characterId
    )

    fun fromEvent(event: ChatInputInteractionEvent) = GameEnrollmentChangeRequest(
        serverId = eventPropertiesMapper.getServerId(event),
        gameSlug = eventPropertiesMapper.getGameSlug(event),
        characterSlug = eventPropertiesMapper.getCharacterSlug(event)
    )

}