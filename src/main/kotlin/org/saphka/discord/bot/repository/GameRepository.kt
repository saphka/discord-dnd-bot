package org.saphka.discord.bot.repository

import org.saphka.discord.bot.domain.Game
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.util.UUID

interface GameRepository : R2dbcRepository<Game, UUID> {

    fun findByServerIdAndStartsAtAfter(serverId: Long, startsAt: LocalDateTime): Flux<Game>

}