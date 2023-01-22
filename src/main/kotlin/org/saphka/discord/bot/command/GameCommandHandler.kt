package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.GameMapper
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GameCommandHandler(
    private val messageSource: MessageSource,
    private val service: GameService,
    private val mapper: GameMapper,
    private val enrollmentCommandHandler: GameEnrollmentCommandHandler,
    private val logCommandHandler: GameLogCommandHandler
) : CommandHandler {

    override fun name(): String {
        return "game"
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return when (event.options.first().name) {
            "create" -> handleCreate(event)
            "list" -> handleList(event)
            "enroll" -> enrollmentCommandHandler.handleEnroll(event)
            "un-enroll" -> enrollmentCommandHandler.handleUnEnroll(event)
            "list-enrolled" -> enrollmentCommandHandler.handleListEnrolled(event)
            "log-add" -> logCommandHandler.handleLogAdd(event)
            "log-list" -> logCommandHandler.handleLogList(event)
            else -> Mono.empty()
        }
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        return service.getUpcomingGames(event.interaction.guildId
            .orElseThrow { IllegalArgumentException("command must be called inside server chat") }
            .asLong())
            .map {
                mapper.toEmbed(it)
            }
            .collectList()
            .flatMap {
                event.reply()
                    .withContent("Upcoming games")
                    .withEmbeds(it).withEphemeral(true)
            }
    }

    private fun handleCreate(event: ChatInputInteractionEvent): Mono<Void> {
        val game = mapper.fromEvent(event)
        return service.create(game).flatMap {
            event.reply()
                .withContent(
                    messageSource.getMessage(
                        "game-announce",
                        arrayOf(it.name),
                        LocaleContextHolder.getLocale()
                    )
                )
        }
    }
}