package org.saphka.discord.bot.repository

import org.saphka.discord.bot.domain.Character
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface CharacterRepository : R2dbcRepository<Character, Long> {

    fun findByServerIdAndOwnerIdOrderByName(serverId: Long, ownerId: Long): Flux<Character>
    fun findByServerIdOrderByName(serverId: Long): Flux<Character>

}