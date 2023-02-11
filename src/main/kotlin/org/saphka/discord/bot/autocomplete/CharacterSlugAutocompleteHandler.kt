package org.saphka.discord.bot.autocomplete

import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import org.saphka.discord.bot.command.FieldName
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.service.CharacterService
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

@Component
class CharacterSlugAutocompleteHandler(
    private val service: CharacterService,
    private val eventPropertiesMapper: EventPropertiesMapper
) : AutocompleteHandler {
    override fun field(): String {
        return FieldName.CHARACTER_SLUG_REF
    }

    override fun handle(event: ChatInputAutoCompleteEvent): Flux<Tuple2<String, String>> {
        val text = event.focusedOption.value.map { it.asString() }.orElse("").lowercase()

        return service.getUserCharactersWithSlugFilter(
            eventPropertiesMapper.getServerId(event),
            eventPropertiesMapper.getUserId(event),
            text
        ).map {
            Tuples.of(it.slug, it.slug)
        }
    }
}