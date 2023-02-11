package org.saphka.discord.bot.service

import org.saphka.discord.bot.configuration.BotProperties
import org.saphka.discord.bot.mapper.GameMapper
import org.saphka.discord.bot.model.GameCreateRequest
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.repository.GameRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class GameService(
    private val repository: GameRepository,
    private val mapper: GameMapper,
    private val properties: BotProperties,
    private val timeAccessor: TimeAccessor
) {
    fun create(game: GameCreateRequest): Mono<GameDTO> {
        return repository.save(
            mapper.toEntity(game)
        ).map {
            mapper.toDto(it)
        }.onErrorMap(DuplicateKeyException::class.java) {
            IllegalArgumentException("game with slug ${game.slug} already exists", it)
        }
    }

    fun getRecentGames(serverId: Long): Flux<GameDTO> {
        return repository.findByServerIdAndStartsAtAfterOrderByStartsAt(serverId, getStartsAtFilter())
            .map { mapper.toDto(it) }
    }

    fun getRecentGamesWithSlugFilter(serverId: Long, slugPrefix: String): Flux<GameDTO> {
        return repository.findByServerIdAndStartsAtAfterAndSlugStartingWithIgnoringCaseOrderByStartsAt(
            serverId,
            getStartsAtFilter(),
            slugPrefix
        ).map { mapper.toDto(it) }
    }

    fun getBySlug(serverId: Long, slug: String): Mono<GameDTO> {
        return repository.findFirstByServerIdAndSlug(serverId, slug)
            .map { mapper.toDto(it) }
            .switchIfEmpty(
                Mono.error {
                    IllegalArgumentException("game with slug $slug not found")
                }
            )
    }

    private fun getStartsAtFilter(): LocalDateTime =
        timeAccessor.getCurrentTime().minus(properties.startsAtBefore).truncatedTo(ChronoUnit.DAYS)

}