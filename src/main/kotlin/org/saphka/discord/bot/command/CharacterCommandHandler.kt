package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.service.CharacterService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class CharacterCommandHandler(
    private val messageSource: MessageSource, private val service: CharacterService, private val mapper: CharacterMapper
) : CommandHandler {

    override fun name(): String {
        return CommandName.CHARACTER_COMMAND
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return when (event.options.first().name) {
            CommandName.CHARACTER_CREATE -> handleCreate(event)
            CommandName.CHARACTER_LIST -> handleList(event)
            else -> Mono.empty()
        }
    }

    fun handleListAll(event: ChatInputInteractionEvent): Mono<Void> {
        return doHandleList(event, service.getCharacters(event.interaction.guildId.orElseThrow {
            IllegalArgumentException(
                messageSource.getMessage(
                    "error-no-server-id", null, Locale.forLanguageTag(event.interaction.userLocale)
                )
            )
        }.asLong()))
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        return doHandleList(event, service.getUserCharacters(event.interaction.guildId.orElseThrow {
            IllegalArgumentException(
                messageSource.getMessage(
                    "error-no-server-id", null, Locale.forLanguageTag(
                        event.interaction.userLocale
                    )
                )
            )
        }.asLong(), event.interaction.user.id.asLong()))
    }

    private fun doHandleList(event: ChatInputInteractionEvent, characters: Flux<CharacterDTO>): Mono<Void> {
        return characters.map {
            mapper.toEmbed(it, Locale.forLanguageTag(event.interaction.userLocale))
        }.collectList().flatMap {
            event.reply().withEmbeds(it).withEphemeral(true).withContent(
                messageSource.getMessage(
                    "character-registered", null, Locale.forLanguageTag(
                        event.interaction.userLocale
                    )
                )
            )
        }
    }

    private fun handleCreate(event: ChatInputInteractionEvent): Mono<Void> {
        val character = mapper.fromEvent(event)
        return service.create(character).flatMap {
            event.reply().withEphemeral(true).withContent(
                messageSource.getMessage(
                    "character-created", arrayOf(it.name), Locale.forLanguageTag(
                        event.interaction.userLocale
                    )
                )
            )
        }
    }
}