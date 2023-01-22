package org.saphka.discord.bot.exception

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun handleException(event: ApplicationCommandInteractionEvent, ex: Throwable): Mono<Void> {
        logger.warn("Exception occurred when handling event {} ", event, ex)

        return event.reply().withContent("Error occurred: ${ex.message}")
    }

}