package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.service.CharacterService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CharacterCommandHandler(
    private val messageSource: MessageSource,
    private val service: CharacterService,
    private val mapper: CharacterMapper
) : CommandHandler {

    override fun name(): String {
        return "character"
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return when (event.options.first().name) {
            "create" -> handleCreate(event)
            "list" -> handleList(event)
            "list-all" -> handleListAll(event)
            else -> Mono.empty()
        }
    }

    private fun handleListAll(event: ChatInputInteractionEvent): Mono<Void> {
        return doHandleList(event, service.getCharacters(event.interaction.guildId
            .orElseThrow { IllegalArgumentException("command must be called inside server chat") }
            .asLong()))
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        return doHandleList(event, service.getUserCharacters(event.interaction.guildId
            .orElseThrow { IllegalArgumentException("command must be called inside server chat") }
            .asLong(),
            event.interaction.user.id.asLong()))
    }

    private fun doHandleList(event: ChatInputInteractionEvent, characters: Flux<CharacterDTO>): Mono<Void> {
        return characters.map {
            mapper.toEmbed(it)
        }
            .collectList()
            .flatMap {
                event.reply().withEmbeds(it).withEphemeral(true).withContent("Registered characters")
            }
    }

    private fun handleCreate(event: ChatInputInteractionEvent): Mono<Void> {
        val character = mapper.fromEvent(event)
        return service.create(character).flatMap {
            event.reply()
                .withEphemeral(true)
                .withContent(
                    messageSource.getMessage(
                        "character-created",
                        arrayOf(it.name),
                        LocaleContextHolder.getLocale()
                    )
                )
        }
    }
}