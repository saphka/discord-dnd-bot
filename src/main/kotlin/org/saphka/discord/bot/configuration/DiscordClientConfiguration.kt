package org.saphka.discord.bot.configuration

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.ReactiveEventAdapter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Duration

@ConfigurationProperties(prefix = "bot")
data class BotProperties(
    val appId: Long,
    val token: String,
    val startsAtBefore: Duration,
    val embedsSize: Int
)

@Configuration
@EnableConfigurationProperties(BotProperties::class)
class DiscordClientConfiguration {

    @Bean
    @Profile("prod")
    fun discordClient(properties: BotProperties, adapters: List<ReactiveEventAdapter>): GatewayDiscordClient {
        val client = DiscordClient
            .create(properties.token)
            .login()
            .block()!!

        adapters.forEach { client.on(it).subscribe() }

        return client
    }

    @Bean
    @Profile("prod")
    fun restClient(discordClient: GatewayDiscordClient) = discordClient.restClient

}