package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.mapper.EventPropertiesMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.model.GameEnrollmentDTO
import org.saphka.discord.bot.service.CharacterService
import org.saphka.discord.bot.service.GameEnrollmentService
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Component
class GameEnrollmentCommandHandler(
    private val service: GameEnrollmentService,
    private val gameService: GameService,
    private val characterService: CharacterService,
    private val characterMapper: CharacterMapper,
    private val eventPropertiesMapper: EventPropertiesMapper,
    private val messageSource: MessageSource
) {

    fun handleEnroll(event: ChatInputInteractionEvent): Mono<Void> {
        return getGameAndCharacter(event).flatMap {
            service.enroll(
                GameEnrollmentDTO(
                    serverId = eventPropertiesMapper.getServerId(event), gameId = it.t1.id!!, characterId = it.t2.id!!
                )
            )
        }.flatMap {
            event.reply().withEphemeral(true).withContent(
                messageSource.getMessage(
                    "enrolled", null, eventPropertiesMapper.getLocale(event)
                )
            )
        }
    }

    fun handleUnEnroll(event: ChatInputInteractionEvent): Mono<Void> {
        return getGameAndCharacter(event).flatMap {
            service.unEnroll(
                GameEnrollmentDTO(
                    serverId = eventPropertiesMapper.getServerId(event), gameId = it.t1.id!!, characterId = it.t2.id!!
                )
            )
        }.flatMap {
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
        return gameService.getBySlug(serverId, gameSlug).flatMapMany {
            service.getEnrolled(serverId, it.id!!)
        }.map { it.characterId }.transform { characterService.getByIds(it) }
            .map { characterMapper.toEmbed(it, eventPropertiesMapper.getLocale(event)) }.collectList()
            .flatMap {
                val reply = event.reply().withEphemeral(true)
                if (it.size > 0) {
                    reply.withEmbeds(it)
                } else {
                    reply.withContent(
                        messageSource.getMessage(
                            "no-enrollments", null, eventPropertiesMapper.getLocale(event)
                        )
                    )
                }
            }
    }

    private fun getGameAndCharacter(event: ChatInputInteractionEvent): Mono<Tuple2<GameDTO, CharacterDTO>> {
        val gameSlug = eventPropertiesMapper.getGameSlug(event)
        val characterSlug = eventPropertiesMapper.getCharacterSlug(event)
        val serverId = eventPropertiesMapper.getServerId(event)

        return Mono.zip(
            gameService.getBySlug(serverId, gameSlug), characterService.getBySlug(serverId, characterSlug)
        )
    }
}