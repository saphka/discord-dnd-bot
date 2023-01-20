package org.saphka.discord.bot.listener

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.command.CommandHandler
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class ApplicationCommandListener(
    private val client: GatewayDiscordClient,
    private val handlers: List<CommandHandler>
) : InitializingBean {

    lateinit var handlersMap: Map<String, CommandHandler>

    override fun afterPropertiesSet() {
        handlersMap = handlers.groupBy(CommandHandler::name).mapValues { it.value.first() }

        client.on(ChatInputInteractionEvent::class.java) {
            handlersMap[it.commandName]?.handle(it)
        }.subscribe()
    }


}