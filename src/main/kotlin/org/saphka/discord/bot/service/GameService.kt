package org.saphka.discord.bot.service

import org.saphka.discord.bot.domain.Game
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.repository.GameRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@Service
class GameService(
    private val repository: GameRepository
) {
    fun create(game: GameDTO): Mono<GameDTO> {
        return repository.save(
            Game(
                game.id,
                game.serverId,
                game.slug,
                game.name,
                game.startsAt,
                game.tier
            )
        ).map {
            toDto(it)
        }.onErrorMap(DuplicateKeyException::class.java) {
            IllegalArgumentException("game with slug ${game.slug} already exists", it)
        }
    }

    fun getUpcomingGames(serverId: Long): Flux<GameDTO> {
        return repository.findByServerIdAndStartsAtAfter(serverId, LocalDateTime.now())
            .map { toDto(it) }
    }

    private fun toDto(it: Game) = GameDTO(
        it.id,
        it.serverId,
        it.slug,
        it.name,
        it.startsAt,
        it.tier
    )
}