package org.saphka.discord.bot.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.model.GameDTO
import org.saphka.discord.bot.model.GameEnrollmentDTO
import org.saphka.discord.bot.service.CharacterService
import org.saphka.discord.bot.service.GameEnrollmentService
import org.saphka.discord.bot.service.GameService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Component
class GameEnrollmentCommandHandler(
    private val service: GameEnrollmentService,
    private val gameService: GameService,
    private val characterService: CharacterService,
    private val characterMapper: CharacterMapper,
    private val messageSource: MessageSource
) {

    fun handleEnroll(event: ChatInputInteractionEvent): Mono<Void> {
        return getGameAndCharacter(event)
            .flatMap {
                service.enroll(
                    GameEnrollmentDTO(
                        serverId = getServerId(event),
                        gameId = it.t1.id!!,
                        characterId = it.t2.id!!
                    )
                )
            }
            .flatMap {
                event.reply().withEphemeral(true)
                    .withContent(messageSource.getMessage("enrolled", null, LocaleContextHolder.getLocale()))
            }
    }

    fun handleUnEnroll(event: ChatInputInteractionEvent): Mono<Void> {
        return getGameAndCharacter(event)
            .flatMap {
                service.unEnroll(
                    GameEnrollmentDTO(
                        serverId = getServerId(event),
                        gameId = it.t1.id!!,
                        characterId = it.t2.id!!
                    )
                )
            }
            .flatMap {
                event.reply().withEphemeral(true)
                    .withContent(messageSource.getMessage("un-enrolled", null, LocaleContextHolder.getLocale()))
            }
    }

    fun handleListEnrolled(event: ChatInputInteractionEvent): Mono<Void> {
        val serverId = getServerId(event)
        val gameSlug =
            event.options.first().getOption("game-slug").flatMap { it.value }.map { it.asString() }.orElse("")
        return gameService.getBySlug(serverId, gameSlug)
            .flatMapMany {
                service.getEnrolled(serverId, it.id!!)
            }
            .map { it.characterId }
            .transform { characterService.getByIds(it) }
            .map { characterMapper.toEmbed(it) }
            .collectList()
            .flatMap {
                val reply = event.reply().withEphemeral(true)
                if (it.size > 0) {
                    reply.withEmbeds(it)
                } else {
                    reply.withContent(messageSource.getMessage("no-enrollments", null, LocaleContextHolder.getLocale()))
                }

            }
    }

    private fun getGameAndCharacter(event: ChatInputInteractionEvent): Mono<Tuple2<GameDTO, CharacterDTO>> {
        val options = event.options.first()
        val gameSlug = options.getOption("game-slug").flatMap { it.value }.map { it.asString() }.orElse("")
        val characterSlug = options.getOption("character-slug").flatMap { it.value }.map { it.asString() }.orElse("")
        val serverId = getServerId(event)

        return Mono.zip(
            gameService.getBySlug(serverId, gameSlug), characterService.getBySlug(serverId, characterSlug)
        )
    }

    private fun getServerId(event: ChatInputInteractionEvent): Long {
        return event.interaction.guildId.orElseThrow {
            IllegalArgumentException(
                messageSource.getMessage(
                    "error-no-server-id",
                    null,
                    LocaleContextHolder.getLocale()
                )
            )
        }.asLong()
    }
}