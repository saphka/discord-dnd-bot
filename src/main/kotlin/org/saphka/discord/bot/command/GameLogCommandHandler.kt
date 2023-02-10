package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.mapper.GameLogMapper
import org.saphka.discord.bot.service.GameLogService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GameLogCommandHandler(
    private val service: GameLogService,
    private val mapper: GameLogMapper,
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val messageSource: MessageSource
) {

    fun handleLogAdd(event: ChatInputInteractionEvent): Mono<Void> {
        return service.addEntry(mapper.fromEvent(event))
            .flatMap {
                event.reply().withEphemeral(true).withContent(
                    messageSource.getMessage("game-log-added", null, eventPropertiesMapper.getLocale(event))
                )
            }
    }

    fun handleLogList(event: ChatInputInteractionEvent): Mono<Void> {
        val serverId = eventPropertiesMapper.getServerId(event)
        val gameSlug = eventPropertiesMapper.getGameSlug(event)

        return service.getEntries(serverId, gameSlug)
            .map {
                it.t1.joinToString("\n") { log ->
                    mapper.toMessage(log, it.t2[log.characterId])
                }
            }.flatMap {
                val header = messageSource.getMessage(
                    "game-log-header",
                    null,
                    eventPropertiesMapper.getLocale(event)
                )
                event.reply().withEphemeral(true).withContent("${header}:\n${it}")
            }
    }
}