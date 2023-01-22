package org.saphka.discord.bot.service

import org.saphka.discord.bot.mapper.GameEnrollmentMapper
import org.saphka.discord.bot.model.GameEnrollmentDTO
import org.saphka.discord.bot.repository.GameEnrollmentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GameEnrollmentService(
    private val repository: GameEnrollmentRepository,
    private val mapper: GameEnrollmentMapper
) {

    fun enroll(enrollment: GameEnrollmentDTO): Mono<GameEnrollmentDTO> {
        return repository.findByServerIdAndGameIdAndCharacterId(
            serverId = enrollment.serverId,
            gameId = enrollment.gameId,
            characterId = enrollment.characterId
        ).switchIfEmpty(repository.save(mapper.toEntity(enrollment)))
            .map { mapper.toDto(it) }
    }

    fun unEnroll(enrollment: GameEnrollmentDTO): Mono<Long> {
        return repository.deleteByServerIdAndGameIdAndCharacterId(
            serverId = enrollment.serverId,
            gameId = enrollment.gameId,
            characterId = enrollment.characterId
        )
    }

    fun getEnrolled(serverId: Long, gameId: Long): Flux<GameEnrollmentDTO> {
        return repository.findByServerIdAndGameId(
            serverId = serverId,
            gameId = gameId,
        ).map { mapper.toDto(it) }
    }


}