package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import reactor.core.publisher.Mono


interface CommandHandler {
    fun name(): String

    fun handle(event: ChatInputInteractionEvent): Mono<Void>
}