package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import org.saphka.discord.bot.command.FieldName
import org.saphka.discord.bot.domain.Character
import org.saphka.discord.bot.model.CharacterDTO
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class CharacterMapper(
    private val messageSource: MessageSource,
    private val eventPropertiesMapper: EventPropertiesMapper
) {

    fun toDto(it: Character) = CharacterDTO(
        id = it.id,
        serverId = it.serverId,
        ownerId = it.ownerId,
        slug = it.slug,
        name = it.name,
        sheet = it.sheet,
        avatar = it.avatar
    )

    fun toEntity(it: CharacterDTO) = Character(
        id = it.id,
        serverId = it.serverId,
        ownerId = it.ownerId,
        slug = it.slug.lowercase(Locale.getDefault()),
        name = it.name,
        sheet = it.sheet,
        avatar = it.avatar
    )

    fun toEmbed(it: CharacterDTO, locale: Locale) = EmbedCreateSpec.builder()
        .title(it.name)
        .url(it.sheet)
        .image(it.avatar)
        .addField(messageSource.getMessage(FieldName.CHARACTER_SLUG, null, locale), it.slug, true)
        .build()

    fun fromEvent(event: ChatInputInteractionEvent): CharacterDTO {
        val options = event.options.first()
        return CharacterDTO(
            serverId = eventPropertiesMapper.getServerId(event),
            ownerId = eventPropertiesMapper.getUserId(event),
            slug = options.getOption(FieldName.CHARACTER_SLUG).flatMap { it.value }.map { it.asString() }
                .orElse(""),
            name = options.getOption(FieldName.CHARACTER_NAME).flatMap { it.value }.map { it.asString() }.orElse(""),
            sheet = options.getOption(FieldName.CHARACTER_SHEET_URL).flatMap { it.value }.map { it.asString() }
                .orElse(""),
            avatar = options.getOption(FieldName.CHARACTER_AVATAR_URL).flatMap { it.value }.map { it.asString() }
                .orElse(""),
        )
    }

}