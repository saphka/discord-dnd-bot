package org.saphka.discord.bot.mapper

import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono
import discord4j.core.spec.InteractionFollowupCreateMono
import org.saphka.discord.bot.configuration.BotProperties
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.function.Supplier

@Component
class EmbedMapper(
    private val properties: BotProperties
) {
    fun splitAndChain(
        reply: InteractionApplicationCommandCallbackReplyMono,
        embeds: List<EmbedCreateSpec>,
        followUpCreator: Supplier<InteractionFollowupCreateMono>
    ): Mono<Void> {
        if (embeds.isNotEmpty()) {
            val chunks = embeds.chunked(properties.embedsSize)
            var result: Mono<Void> = reply.withEmbeds(chunks.first())
            result = chunks.drop(1).fold(result) { acc, chunk ->
                acc.then(followUpCreator.get().withEmbeds(chunk).then())
            }
            return result
        }
        return reply
    }
}