package org.saphka.discord.bot.repository

import org.saphka.discord.bot.domain.Game
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface GameRepository : R2dbcRepository<Game, Long> {

    fun findByServerIdAndStartsAtAfterOrderByStartsAt(serverId: Long, startsAt: LocalDateTime): Flux<Game>

    fun findByServerIdAndStartsAtAfterAndSlugStartingWithIgnoringCaseOrderByStartsAt(
        serverId: Long,
        startsAt: LocalDateTime,
        slugPrefix: String
    ): Flux<Game>

    fun findFirstByServerIdAndSlug(serverId: Long, slug: String): Mono<Game>

}