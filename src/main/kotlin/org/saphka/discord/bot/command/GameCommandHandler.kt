package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.EmbedMapper
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.mapper.GameMapper
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GameCommandHandler(
    private val messageSource: MessageSource,
    private val service: GameService,
    private val mapper: GameMapper,
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val embedMapper: EmbedMapper,
    private val enrollmentCommandHandler: GameEnrollmentCommandHandler
) : CommandHandler {

    override fun name(): String {
        return CommandName.GAME_COMMAND
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return when (event.options.first().name) {
            CommandName.GAME_LIST -> handleList(event)
            CommandName.GAME_ENROLL -> enrollmentCommandHandler.handleEnroll(event)
            CommandName.GAME_UN_ENROLL -> enrollmentCommandHandler.handleUnEnroll(event)
            else -> Mono.empty()
        }
    }

    fun handleCreate(event: ChatInputInteractionEvent): Mono<Void> {
        val game = mapper.fromEvent(event)
        return service.create(game).flatMap {
            event.reply().withContent(
                messageSource.getMessage(
                    "game-announce",
                    arrayOf(it.name, mapper.formatGameTime(it.startsAt)),
                    eventPropertiesMapper.getLocale(event)
                )
            )
        }
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        val locale = eventPropertiesMapper.getLocale(event)
        return service.getRecentGames(eventPropertiesMapper.getServerId(event)).map {
            mapper.toEmbed(it, locale)
        }.collectList().flatMap {
            embedMapper.splitAndChain(
                event.reply().withContent(
                    messageSource.getMessage("game-list-header", null, locale)
                ).withEphemeral(true),
                it,
                event::createFollowup
            )
        }
    }
}