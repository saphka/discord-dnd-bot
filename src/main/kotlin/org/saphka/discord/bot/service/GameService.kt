package org.saphka.discord.bot.service

import org.saphka.discord.bot.mapper.GameMapper
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.repository.GameRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class GameService(
    private val repository: GameRepository,
    private val mapper: GameMapper
) {
    fun create(game: GameDTO): Mono<GameDTO> {
        return repository.save(
            mapper.toEntity(game)
        ).map {
            mapper.toDto(it)
        }.onErrorMap(DuplicateKeyException::class.java) {
            IllegalArgumentException("game with slug ${game.slug} already exists", it)
        }
    }

    fun getUpcomingGames(serverId: Long): Flux<GameDTO> {
        return repository.findByServerIdAndStartsAtAfter(serverId, LocalDateTime.now())
            .map { mapper.toDto(it) }
    }

    fun getBySlug(serverId: Long, slug: String): Mono<GameDTO> {
        return repository.findFirstByServerIdAndSlug(serverId, slug).map { mapper.toDto(it) }
    }
}