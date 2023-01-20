package org.saphka.discord.bot.configuration

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
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
    fun discordClient(properties: BotProperties) = DiscordClient
        .create(properties.token)
        .login()
        .block()

    @Bean
    fun restClient(discordClient: GatewayDiscordClient) = discordClient.restClient

}