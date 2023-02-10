package org.saphka.discord.bot.service

import org.saphka.discord.bot.mapper.GameLogMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameLogCreateRequest
import org.saphka.discord.bot.model.GameLogDTO
import org.saphka.discord.bot.repository.GameLogRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Service
class GameLogService(
    private val repository: GameLogRepository,
    private val mapper: GameLogMapper,
    private val gameService: GameService,
    private val characterService: CharacterService
) {

    fun addEntry(entry: GameLogCreateRequest): Mono<GameLogDTO> {
        return Mono.zip(
            gameService.getBySlug(entry.serverId, entry.gameSlug),
            characterService.getBySlug(entry.serverId, entry.characterSlug)
        ).flatMap {
            repository.save(mapper.toEntity(entry, it.t1, it.t2))
                .map { log -> mapper.toDto(log) }
        }
    }

    fun getEntries(serverId: Long, gameSlug: String): Mono<Tuple2<List<GameLogDTO>, Map<Long, CharacterDTO>>> {
        return gameService.getBySlug(serverId, gameSlug)
            .flatMapMany { game ->
                repository.findByServerIdAndGameId(serverId, game.id)
                    .map { mapper.toDto(it) }
            }.collectList().zipWhen { log ->
                characterService.getByIds(Flux.fromIterable((log).map { it.characterId })).collectMap { it.id }
            }
    }

}