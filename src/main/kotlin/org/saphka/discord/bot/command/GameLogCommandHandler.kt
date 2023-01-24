package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.mapper.GameLogMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.service.CharacterService
import org.saphka.discord.bot.service.GameLogService
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Component
class GameLogCommandHandler(
    private val service: GameLogService,
    private val mapper: GameLogMapper,
    private val gameService: GameService,
    private val characterService: CharacterService,
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val messageSource: MessageSource
) {

    fun handleLogAdd(event: ChatInputInteractionEvent): Mono<Void> {
        return getGameAndCharacter(event).map { mapper.fromEvent(event, it.t1, it.t2) }.flatMap { service.addEntry(it) }
            .flatMap {
                event.reply().withEphemeral(true).withContent(
                    messageSource.getMessage("game-log-added", null, eventPropertiesMapper.getLocale(event))
                )
            }
    }

    fun handleLogList(event: ChatInputInteractionEvent): Mono<Void> {
        val serverId = eventPropertiesMapper.getServerId(event)
        val gameSlug = eventPropertiesMapper.getGameSlug(event)

        return gameService.getBySlug(serverId, gameSlug).flatMapMany { service.getEntries(serverId, it.id!!) }
            .collectList().zipWhen { log ->
                characterService.getByIds(Flux.fromIterable((log).map { it.characterId })).collectMap { it.id!! }
            }.map {
                it.t1.joinToString("\n") { log ->
                    mapper.toMessage(log, it.t2[log.characterId])
                }
            }.flatMap {
                val header = messageSource.getMessage(
                    "game-log-header",
                    null,
                    eventPropertiesMapper.getLocale(event)
                )
                event.reply().withEphemeral(true).withContent("${header}:\n${it}")
            }
    }

    private fun getGameAndCharacter(event: ChatInputInteractionEvent): Mono<Tuple2<GameDTO, CharacterDTO>> {
        val gameSlug = eventPropertiesMapper.getGameSlug(event)
        val characterSlug = eventPropertiesMapper.getCharacterSlug(event)
        val serverId = eventPropertiesMapper.getServerId(event)

        return Mono.zip(
            gameService.getBySlug(serverId, gameSlug), characterService.getBySlug(serverId, characterSlug)
        )
    }
}