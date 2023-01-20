package org.saphka.discord.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiscordBotApplication {
}

fun main(args: Array<String>) {
    runApplication<DiscordBotApplication>(*args)
}