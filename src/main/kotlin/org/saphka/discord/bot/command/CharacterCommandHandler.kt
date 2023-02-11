package org.saphka.discord.bot.command

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.service.CharacterService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

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

        return characters.map {
            Snowflake.of(it.ownerId)
        }.collect(Collectors.toSet())
            .flatMapMany { event.client.requestMembers(Snowflake.of(serverId), it) }
            .collectMap { it.id.asLong() }
            .zipWith(characters.collectList())
            .flatMap {
                val embeds = it.t2.map { characterDTO ->
                    mapper.toEmbed(characterDTO, it.t1[characterDTO.ownerId], locale)
                }

                event.reply().withEmbeds(embeds).withEphemeral(true).withContent(
                    messageSource.getMessage("character-registered", null, locale)
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