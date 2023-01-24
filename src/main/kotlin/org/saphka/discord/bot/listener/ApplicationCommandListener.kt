package org.saphka.discord.bot.listener

import discord4j.core.event.ReactiveEventAdapter
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import org.reactivestreams.Publisher
import org.saphka.discord.bot.autocomplete.AutocompleteHandler
import org.saphka.discord.bot.command.CommandHandler
import org.saphka.discord.bot.exception.ExceptionHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ApplicationCommandListener constructor(
    commandHandlers: List<CommandHandler>,
    autocompleteHandlers: List<AutocompleteHandler>,
    private val exceptionHandler: ExceptionHandler
) : ReactiveEventAdapter() {

    private val commandHandlerMap: Map<String, CommandHandler>
    private val autocompleteHandlerMap: Map<String, AutocompleteHandler>

    init {
        this.commandHandlerMap = commandHandlers.groupBy(CommandHandler::name).mapValues { it.value.first() }
        this.autocompleteHandlerMap =
            autocompleteHandlers.groupBy(AutocompleteHandler::field).mapValues { it.value.first() }
    }

    override fun onChatInputInteraction(event: ChatInputInteractionEvent): Publisher<*> {
        return commandHandlerMap[event.commandName]?.handle(event)?.onErrorResume {
            exceptionHandler.handleException(event, it)
        } ?: Mono.empty<Void>()
    }

    override fun onChatInputAutoCompleteInteraction(event: ChatInputAutoCompleteEvent): Publisher<*> {
        val handler = autocompleteHandlerMap[event.focusedOption.name] ?: return Mono.empty<Void>()

        return handler.handle(event)
            .map {
                ApplicationCommandOptionChoiceData.builder()
                    .name(it.t1)
                    .value(it.t2)
                    .build()
            }
            .collectList()
            .flatMap {
                event.respondWithSuggestions(it)
            }
    }
}