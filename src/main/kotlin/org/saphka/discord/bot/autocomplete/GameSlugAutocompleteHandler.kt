package org.saphka.discord.bot.autocomplete

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import org.saphka.discord.bot.command.FieldName
import org.saphka.discord.bot.service.GameService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GameSlugAutocompleteHandler(
    private val service: GameService
) : AutocompleteHandler {
    override fun field(): String {
        return FieldName.GAME_SLUG_REF
    }

    override fun handle(event: ChatInputAutoCompleteEvent): Mono<Void> {
        val text = event.focusedOption.value.map { it.asString() }.orElse("").lowercase()

        return service.getUpcomingGames(event.interaction.guildId.orElseThrow().asLong())
            .filter { it.slug.startsWith(text) }
            .map {
                ApplicationCommandOptionChoiceData.builder()
                    .name(it.slug)
                    .value(it.slug)
                    .build()
            }
            .collectList()
            .flatMap {
                event.respondWithSuggestions(it)
            }
    }
}