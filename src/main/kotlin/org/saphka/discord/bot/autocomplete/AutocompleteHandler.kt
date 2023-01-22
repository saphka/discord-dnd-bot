package org.saphka.discord.bot.autocomplete

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import reactor.core.publisher.Mono

interface AutocompleteHandler {
    fun field(): String

    fun handle(event: ChatInputAutoCompleteEvent): Mono<Void>

}