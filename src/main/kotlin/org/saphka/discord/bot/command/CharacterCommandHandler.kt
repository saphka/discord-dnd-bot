package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.service.CharacterService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CharacterCommandHandler(
    private val messageSource: MessageSource,
    private val service: CharacterService,
    private val mapper: CharacterMapper,
    private val eventPropertiesMapper: EventPropertiesMapper
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
        return doHandleList(event, service.getCharacters(eventPropertiesMapper.getServerId(event)))
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        return doHandleList(
            event, service.getUserCharacters(
                eventPropertiesMapper.getServerId(event), eventPropertiesMapper.getUserId((event))
            )
        )
    }

    private fun doHandleList(event: ChatInputInteractionEvent, characters: Flux<CharacterDTO>): Mono<Void> {
        return characters.map {
            mapper.toEmbed(it, eventPropertiesMapper.getLocale(event))
        }.collectList().flatMap {
            event.reply().withEmbeds(it).withEphemeral(true).withContent(
                messageSource.getMessage("character-registered", null, eventPropertiesMapper.getLocale(event))
            )
        }
    }

    private fun handleCreate(event: ChatInputInteractionEvent): Mono<Void> {
        val character = mapper.fromEvent(event)
        return service.create(character).flatMap {
            event.reply().withEphemeral(true).withContent(
                messageSource.getMessage("character-created", arrayOf(it.name), eventPropertiesMapper.getLocale(event))
            )
        }
    }
}