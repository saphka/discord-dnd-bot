package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.mapper.EmbedMapper
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
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val embedMapper: EmbedMapper
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
        val serverId = eventPropertiesMapper.getServerId(event)
        return doHandleList(event, serverId, service.getCharacters(serverId))
    }

    private fun handleList(event: ChatInputInteractionEvent): Mono<Void> {
        val serverId = eventPropertiesMapper.getServerId(event)
        return doHandleList(
            event, serverId, service.getUserCharacters(
                serverId, eventPropertiesMapper.getUserId((event))
            )
        )
    }

    private fun doHandleList(
        event: ChatInputInteractionEvent,
        serverId: Long,
        characters: Flux<CharacterDTO>
    ): Mono<Void> {
        val locale = eventPropertiesMapper.getLocale(event)

        return mapper.toEmbed(
            characters,
            serverId,
            event.client,
            locale
        ).flatMap { embeds ->
            embedMapper.splitAndChain(
                event.reply().withEphemeral(true).withContent(
                    messageSource.getMessage("character-registered", null, locale)
                ),
                embeds,
                event::createFollowup
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