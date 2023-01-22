package org.saphka.discord.bot.mapper

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import org.saphka.discord.bot.domain.Character
import org.saphka.discord.bot.model.CharacterDTO
import org.springframework.stereotype.Component
import java.util.*

@Component
class CharacterMapper {

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

    fun toEmbed(it: CharacterDTO) = EmbedCreateSpec.builder()
        .title(it.name)
        .url(it.sheet)
        .image(it.avatar)
        .addField("Slug", it.slug, true)
        .build()

    fun fromEvent(event: ChatInputInteractionEvent): CharacterDTO {
        val options = event.options.first()
        return CharacterDTO(
            serverId = event.interaction.guildId.orElseThrow().asLong(),
            ownerId = event.interaction.user.id.asLong(),
            slug = options.getOption("slug").flatMap { it.value }.map { it.asString() }.orElse(""),
            name = options.getOption("name").flatMap { it.value }.map { it.asString() }.orElse(""),
            sheet = options.getOption("sheet").flatMap { it.value }.map { it.asString() }.orElse(""),
            avatar = options.getOption("avatar").flatMap { it.value }.map { it.asString() }.orElse(""),
        )
    }

}