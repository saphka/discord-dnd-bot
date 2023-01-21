package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Component
class GameCommandHandler(
    private val messageSource: MessageSource,
    private val gameService: GameService
) : CommandHandler {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    override fun name(): String {
        return "game"
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return when (event.options.first().name) {
            "create" -> handleCreate(event)
            "list" -> handleList(event)
            else -> Mono.empty()
        }
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        return gameService.getUpcomingGames(event.interaction.guildId.orElseThrow().asLong())
            .map {
                EmbedCreateSpec.builder()
                    .title(it.name)
                    .timestamp(it.startsAt.toInstant(ZoneOffset.UTC))
                    .addField("Tier", it.tier, true)
                    .addField("Time", it.startsAt.toLocalTime().toString(), true)
                    .build()
            }
            .collectList()
            .flatMap {
                event.reply().withEmbeds(it).withEphemeral(true)
            }
    }

    private fun handleCreate(event: ChatInputInteractionEvent): Mono<Void> {
        val options = event.options.first()
        val game = GameDTO(
            serverId = event.interaction.guildId.orElseThrow().asLong(),
            slug = options.getOption("slug").flatMap { it.value }.map { it.asString() }.orElse(""),
            name = options.getOption("name").flatMap { it.value }.map { it.asString() }.orElse(""),
            startsAt = options.getOption("date").flatMap { it.value }.map { it.asString() }
                .map { LocalDateTime.parse(it, formatter) }
                .orElse(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1)),
            tier = options.getOption("tier").flatMap { it.value }.map { it.asString() }.orElse(""),
        )

        return gameService.create(game).flatMap {
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