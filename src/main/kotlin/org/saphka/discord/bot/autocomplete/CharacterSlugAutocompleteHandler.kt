package org.saphka.discord.bot.autocomplete

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import org.saphka.discord.bot.service.CharacterService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CharacterSlugAutocompleteHandler(
    private val service: CharacterService
) : AutocompleteHandler {
    override fun field(): String {
        return "character-slug"
    }

    override fun handle(event: ChatInputAutoCompleteEvent): Mono<Void> {
        val text = event.focusedOption.value.map { it.asString() }.orElse("").lowercase()

        return service.getUserCharacters(
            event.interaction.guildId.orElseThrow().asLong(),
            event.interaction.user.id.asLong()
        )
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