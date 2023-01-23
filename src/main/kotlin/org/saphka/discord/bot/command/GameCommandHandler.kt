package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.GameMapper
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class GameCommandHandler(
    private val messageSource: MessageSource,
    private val service: GameService,
    private val mapper: GameMapper,
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
                    arrayOf(it.name, it.startsAt.format(mapper.formatter)),
                    Locale.forLanguageTag(event.interaction.userLocale)
                )
            )
        }
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        return service.getUpcomingGames(event.interaction.guildId.orElseThrow {
            IllegalArgumentException(
                messageSource.getMessage(
                    "error-no-server-id", null, Locale.forLanguageTag(event.interaction.userLocale)
                )
            )
        }.asLong()).map {
            mapper.toEmbed(it, Locale.forLanguageTag(event.interaction.userLocale))
        }.collectList().flatMap {
            event.reply().withContent(
                messageSource.getMessage(
                    "game-list-header", null, Locale.forLanguageTag(event.interaction.userLocale)
                )
            ).withEmbeds(it).withEphemeral(true)
        }
    }
}