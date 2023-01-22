package org.saphka.discord.bot.service

import org.saphka.discord.bot.mapper.GameLogMapper
import org.saphka.discord.bot.model.GameLogDTO
import org.saphka.discord.bot.repository.GameLogRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GameLogService(
    private val repository: GameLogRepository,
    private val mapper: GameLogMapper
) {

    fun addEntry(entry: GameLogDTO): Mono<GameLogDTO> {
        return repository.save(mapper.toEntity(entry))
            .map { mapper.toDto(it) }
    }

    fun getEntries(serverId: Long, gameId: Long): Flux<GameLogDTO> {
        return repository.findByServerIdAndGameId(serverId, gameId)
            .map { mapper.toDto(it) }
    }

}