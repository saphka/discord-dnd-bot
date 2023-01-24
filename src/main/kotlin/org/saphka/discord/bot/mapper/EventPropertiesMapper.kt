package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.interaction.InteractionCreateEvent
import org.saphka.discord.bot.command.FieldName
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class EventPropertiesMapper(
    private val messageSource: MessageSource
) {

    fun getServerId(event: InteractionCreateEvent) = event.interaction.guildId.orElseThrow {
        IllegalArgumentException(
            messageSource.getMessage("error-no-server-id", null, getLocale(event))
        )
    }.asLong()

    fun getUserId(event: InteractionCreateEvent) = event.interaction.user.id.asLong()

    fun getLocale(event: InteractionCreateEvent): Locale =
        Locale.forLanguageTag(event.interaction.userLocale)

    fun getGameSlug(event: ChatInputInteractionEvent): String =
        event.options.first().getOption(FieldName.GAME_SLUG_REF).flatMap { it.value }.map { it.asString() }
            .orElse("")

    fun getCharacterSlug(event: ChatInputInteractionEvent): String =
        event.options.first().getOption(FieldName.CHARACTER_SLUG_REF).flatMap { it.value }.map { it.asString() }
            .orElse("")

}