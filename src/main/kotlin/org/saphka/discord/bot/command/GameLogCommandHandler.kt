package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.GameLogMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.service.CharacterService
import org.saphka.discord.bot.service.GameLogService
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
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
    private val messageSource: MessageSource
) {

    fun handleLogAdd(event: ChatInputInteractionEvent): Mono<Void> {
        return getGameAndCharacter(event)
            .map { mapper.fromEvent(event, it.t1, it.t2) }
            .flatMap { service.addEntry(it) }
            .flatMap {
                event.reply().withEphemeral(true)
                    .withContent(messageSource.getMessage("game-log-added", null, LocaleContextHolder.getLocale()))
            }
    }

    fun handleLogList(event: ChatInputInteractionEvent): Mono<Void> {
        val serverId = getServerId(event)
        val gameSlug =
            event.options.first().getOption(FieldName.GAME_SLUG_REF).flatMap { it.value }.map { it.asString() }
                .orElse("")

        return gameService.getBySlug(serverId, gameSlug)
            .flatMapMany { service.getEntries(serverId, it.id!!) }
            .collectList()
            .zipWhen { log ->
                characterService.getByIds(
                    Flux.fromIterable((log)
                        .map { it.characterId }
                    )).collectMap { it.id!! }
            }
            .map {
                it.t1.map { log ->
                    mapper.toMessage(log, it.t2[log.characterId])
                }.joinToString("\n")
            }
            .flatMap {
                val header = messageSource.getMessage("game-log-header", null, LocaleContextHolder.getLocale())

                event.reply().withEphemeral(true)
                    .withContent("${header}:\n${it}")
            }


    }

    private fun getGameAndCharacter(event: ChatInputInteractionEvent): Mono<Tuple2<GameDTO, CharacterDTO>> {
        val options = event.options.first()
        val gameSlug = options.getOption(FieldName.GAME_SLUG_REF).flatMap { it.value }.map { it.asString() }.orElse("")
        val characterSlug =
            options.getOption(FieldName.CHARACTER_SLUG_REF).flatMap { it.value }.map { it.asString() }.orElse("")
        val serverId = getServerId(event)

        return Mono.zip(
            gameService.getBySlug(serverId, gameSlug), characterService.getBySlug(serverId, characterSlug)
        )
    }

    private fun getServerId(event: ChatInputInteractionEvent): Long {
        return event.interaction.guildId.orElseThrow {
            IllegalArgumentException(
                messageSource.getMessage(
                    "error-no-server-id",
                    null,
                    LocaleContextHolder.getLocale()
                )
            )
        }.asLong()
    }

}