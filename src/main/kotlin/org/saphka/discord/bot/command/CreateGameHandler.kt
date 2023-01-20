package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateGameHandler(private val messageSource: MessageSource) : CommandHandler {
    override fun name(): String {
        return "create-game"
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return event.reply().withContent(
            messageSource.getMessage(
                "game-announce",
                arrayOf(
                    event.getOption("name")
                        .flatMap { it.value }
                        .map { it.asString() }
                        .orElse("")),
                LocaleContextHolder.getLocale()))
    }
}