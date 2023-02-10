package org.saphka.discord.bot.service

import org.saphka.discord.bot.mapper.GameEnrollmentMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameEnrollmentChangeRequest
import org.saphka.discord.bot.model.GameEnrollmentDTO
import org.saphka.discord.bot.repository.GameEnrollmentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GameEnrollmentService(
    private val repository: GameEnrollmentRepository,
    private val mapper: GameEnrollmentMapper,
    private val gameService: GameService,
    private val characterService: CharacterService
) {

    fun enroll(enrollment: GameEnrollmentChangeRequest): Mono<GameEnrollmentDTO> {
        return Mono.zip(
            gameService.getBySlug(enrollment.serverId, enrollment.gameSlug),
            characterService.getBySlug(enrollment.serverId, enrollment.characterSlug)
        ).flatMap {
            repository.findByServerIdAndGameIdAndCharacterId(
                serverId = enrollment.serverId,
                gameId = it.t1.id,
                characterId = it.t2.id
            ).switchIfEmpty(
                repository.save(mapper.toEntity(enrollment, it.t1, it.t2))
            ).map { enr -> mapper.toDto(enr) }
        }
    }

    fun unEnroll(enrollment: GameEnrollmentChangeRequest): Mono<Long> {
        return Mono.zip(
            gameService.getBySlug(enrollment.serverId, enrollment.gameSlug),
            characterService.getBySlug(enrollment.serverId, enrollment.characterSlug)
        ).flatMap {
            repository.deleteByServerIdAndGameIdAndCharacterId(
                serverId = enrollment.serverId,
                gameId = it.t1.id,
                characterId = it.t2.id
            )
        }
    }

    fun getEnrolled(serverId: Long, gameSlug: String): Flux<CharacterDTO> {
        return gameService.getBySlug(serverId, gameSlug)
            .flatMapMany { game ->
                repository.findByServerIdAndGameId(
                    serverId = serverId,
                    gameId = game.id,
                ).map { mapper.toDto(it) }
            }
            .map { it.characterId }.transform { characterService.getByIds(it) }
    }


}