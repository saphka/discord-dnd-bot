package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AdminCommandHandler(
    private val gameCommandHandler: GameCommandHandler,
    private val gameEnrollmentCommandHandler: GameEnrollmentCommandHandler,
    private val gameLogCommandHandler: GameLogCommandHandler,
    private val characterCommandHandler: CharacterCommandHandler
) : CommandHandler {
    override fun name(): String {
        return CommandName.ADMIN_COMMAND
    }

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        return when (event.options.first().name) {
            CommandName.ADMIN_CREATE_GAME -> gameCommandHandler.handleCreate(event)
            CommandName.ADMIN_LIST_ENROLLED -> gameEnrollmentCommandHandler.handleListEnrolled(event)
            CommandName.ADMIN_LIST_CHARACTERS -> characterCommandHandler.handleListAll(event)
            CommandName.ADMIN_LOG_ADD -> gameLogCommandHandler.handleLogAdd(event)
            CommandName.ADMIN_LOG_LIST -> gameLogCommandHandler.handleLogList(event)
            else -> Mono.empty()
        }
    }
}