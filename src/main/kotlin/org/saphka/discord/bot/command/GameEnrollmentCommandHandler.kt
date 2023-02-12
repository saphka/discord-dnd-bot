package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.mapper.EmbedMapper
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.mapper.GameEnrollmentMapper
import org.saphka.discord.bot.service.GameEnrollmentService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GameEnrollmentCommandHandler(
    private val service: GameEnrollmentService,
    private val mapper: GameEnrollmentMapper,
    private val characterMapper: CharacterMapper,
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val embedMapper: EmbedMapper,
    private val messageSource: MessageSource
) {

    fun handleEnroll(event: ChatInputInteractionEvent): Mono<Void> {
        return service.enroll(mapper.fromEvent(event))
            .flatMap {
                event.reply().withEphemeral(true).withContent(
                    messageSource.getMessage(
                        "enrolled", null, eventPropertiesMapper.getLocale(event)
                    )
                )
            }
    }

    fun handleUnEnroll(event: ChatInputInteractionEvent): Mono<Void> {
        return service.unEnroll(mapper.fromEvent(event))
            .flatMap {
                event.reply().withEphemeral(true).withContent(
                    messageSource.getMessage(
                        "un-enrolled", null, eventPropertiesMapper.getLocale(event)
                    )
                )
            }
    }

    fun handleListEnrolled(event: ChatInputInteractionEvent): Mono<Void> {
        val serverId = eventPropertiesMapper.getServerId(event)
        val gameSlug = eventPropertiesMapper.getGameSlug(event)
        val locale = eventPropertiesMapper.getLocale(event)

        return characterMapper.toEmbed(
            service.getEnrolled(serverId, gameSlug),
            serverId,
            event.client,
            locale
        ).flatMap { embeds ->
            val reply = event.reply().withEphemeral(true)
            if (embeds.isNotEmpty()) {
                embedMapper.splitAndChain(
                    reply,
                    embeds,
                    event::createFollowup
                )
            } else {
                reply.withContent(
                    messageSource.getMessage(
                        "no-enrollments", null, locale
                    )
                )
            }
        }
    }
}