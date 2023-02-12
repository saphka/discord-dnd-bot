package org.saphka.discord.bot.repository

import org.saphka.discord.bot.domain.GameEnrollment
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface GameEnrollmentRepository : R2dbcRepository<GameEnrollment, Long> {

    fun deleteByServerIdAndGameIdAndCharacterId(serverId: Long, gameId: Long, characterId: Long): Mono<Long>

    fun findByServerIdAndGameIdAndCharacterId(serverId: Long, gameId: Long, characterId: Long): Mono<GameEnrollment>

    fun findByServerIdAndGameId(serverId: Long, gameId: Long): Flux<GameEnrollment>

}