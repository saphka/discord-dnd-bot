package org.saphka.discord.bot.repository

import org.saphka.discord.bot.domain.GameLog
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface GameLogRepository : R2dbcRepository<GameLog, Long> {

    fun findByServerIdAndGameId(serverId: Long, gameId: Long): Flux<GameLog>

}