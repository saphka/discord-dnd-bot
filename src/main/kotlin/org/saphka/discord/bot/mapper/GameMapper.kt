package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import org.saphka.discord.bot.domain.Game
import org.saphka.discord.bot.model.GameDTO
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class GameMapper {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun toEntity(game: GameDTO) = Game(
        id = game.id,
        serverId = game.serverId,
        slug = game.slug.lowercase(Locale.getDefault()),
        name = game.name,
        startsAt = game.startsAt,
        tier = game.tier
    )

    fun toDto(it: Game) = GameDTO(
        id = it.id,
        serverId = it.serverId,
        slug = it.slug,
        name = it.name,
        startsAt = it.startsAt,
        tier = it.tier
    )

    fun toEmbed(it: GameDTO) = EmbedCreateSpec.builder()
        .title(it.name)
        .timestamp(it.startsAt.toInstant(ZoneOffset.UTC))
        .addField("Slug", it.slug, true)
        .addField("Tier", it.tier, true)
        .addField("Time", it.startsAt.toLocalTime().toString(), true)
        .build()

    fun fromEvent(event: ChatInputInteractionEvent): GameDTO {
        val options = event.options.first()
        return GameDTO(
            serverId = event.interaction.guildId.orElseThrow().asLong(),
            slug = options.getOption("slug").flatMap { it.value }.map { it.asString() }.orElse(""),
            name = options.getOption("name").flatMap { it.value }.map { it.asString() }.orElse(""),
            startsAt = options.getOption("date").flatMap { it.value }.map { it.asString() }
                .map { LocalDateTime.parse(it, formatter) }
                .orElse(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1)),
            tier = options.getOption("tier").flatMap { it.value }.map { it.asString() }.orElse(""),
        )
    }

}