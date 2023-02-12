package org.saphka.discord.bot.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import discord4j.common.JacksonResources
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.RestClient
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
@Profile("prod")
class CommandsRegistrar(
    private val restClient: RestClient,
    @Value("classpath:commands/*")
    private val commands: Array<Resource>,
    private val mapper: ObjectMapper,
    private val botProperties: BotProperties,
) : InitializingBean {

    override fun afterPropertiesSet() {
        val specialMapper = JacksonResources.createFromObjectMapper(mapper).objectMapper

        restClient.applicationService.bulkOverwriteGlobalApplicationCommand(
            botProperties.appId,
            commands.map {
                it.inputStream.use { inp ->
                    specialMapper.readValue(inp, ApplicationCommandRequest::class.java)
                }
            }
        ).subscribe()
    }

}