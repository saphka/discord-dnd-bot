package org.saphka.discord.bot.mapper

import org.saphka.discord.bot.domain.GameEnrollment
import org.saphka.discord.bot.model.GameEnrollmentDTO
import org.springframework.stereotype.Component

@Component
class GameEnrollmentMapper {

    fun toEntity(it: GameEnrollmentDTO) = GameEnrollment(
        id = it.id,
        serverId = it.serverId,
        gameId = it.gameId,
        characterId = it.characterId
    )

    fun toDto(it: GameEnrollment) = GameEnrollmentDTO(
        id = it.id,
        serverId = it.serverId,
        gameId = it.gameId,
        characterId = it.characterId
    )

}