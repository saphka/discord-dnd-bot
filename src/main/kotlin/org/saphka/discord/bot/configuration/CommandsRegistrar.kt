package org.saphka.discord.bot.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.common.JacksonResources
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

const val serverId = 1066062957974392893

@Component
class CommandsRegistrar(
    private val restClient: RestClient,
    @Value("classpath:commands/*")
    private val commands: Array<Resource>,
    private val mapper: ObjectMapper,
    private val botProperties: BotProperties,
) : InitializingBean {

    override fun afterPropertiesSet() {
        val specialMapper = JacksonResources.createFromObjectMapper(mapper).objectMapper

        restClient.applicationService.bulkOverwriteGuildApplicationCommand(
            botProperties.appId,
            serverId,
            commands.map {
                it.inputStream.use { inp ->
                    specialMapper.readValue(inp, ApplicationCommandRequest::class.java)
                }
            }.toList()
        ).subscribe()
    }

}