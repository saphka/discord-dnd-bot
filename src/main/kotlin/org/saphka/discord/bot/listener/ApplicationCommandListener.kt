package org.saphka.discord.bot.listener

import discord4j.core.event.ReactiveEventAdapter
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.reactivestreams.Publisher
import org.saphka.discord.bot.command.CommandHandler
import org.saphka.discord.bot.exception.ExceptionHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ApplicationCommandListener constructor(
    handlers: List<CommandHandler>,
    private val exceptionHandler: ExceptionHandler
) : ReactiveEventAdapter() {

    private val handlersMap: Map<String, CommandHandler>

    init {
        this.handlersMap = handlers.groupBy(CommandHandler::name).mapValues { it.value.first() }
    }

    override fun onChatInputInteraction(event: ChatInputInteractionEvent): Publisher<*> {
        return handlersMap[event.commandName]?.handle(event)?.onErrorResume {
            exceptionHandler.handleException(event, it)
        } ?: Mono.empty<Void>()
    }
}