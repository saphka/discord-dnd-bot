package org.saphka.discord.bot.configuration

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.ReactiveEventAdapter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "bot")
data class BotProperties(val appId: Long, val token: String)

@Configuration
@EnableConfigurationProperties(BotProperties::class)
class DiscordClientConfiguration {

    @Bean
    fun discordClient(properties: BotProperties, adapters: List<ReactiveEventAdapter>): GatewayDiscordClient {
        val client = DiscordClient
            .create(properties.token)
            .login()
            .block()!!

        adapters.forEach { client.on(it).subscribe() }

        return client
    }

    @Bean
    fun restClient(discordClient: GatewayDiscordClient) = discordClient.restClient

}