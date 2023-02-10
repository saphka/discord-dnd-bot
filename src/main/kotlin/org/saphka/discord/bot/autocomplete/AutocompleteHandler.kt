package org.saphka.discord.bot.autocomplete

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2

interface AutocompleteHandler {
    fun field(): String

    fun handle(event: ChatInputAutoCompleteEvent): Flux<Tuple2<String, String>>
}