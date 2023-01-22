package org.saphka.discord.bot.service

import org.saphka.discord.bot.mapper.CharacterMapper
import org.saphka.discord.bot.model.CharacterDTO
import org.saphka.discord.bot.repository.CharacterRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CharacterService(
    private val repository: CharacterRepository,
    private val mapper: CharacterMapper
) {

    fun create(character: CharacterDTO): Mono<CharacterDTO> {
        return repository.save(
            mapper.toEntity(character)
        ).map {
            mapper.toDto(it)
        }.onErrorMap(DuplicateKeyException::class.java) {
            IllegalArgumentException("character with slug ${character.slug} already exists", it)
        }
    }

    fun getUserCharacters(serverId: Long, userId: Long): Flux<CharacterDTO> {
        return repository.findByServerIdAndOwnerIdOrderByName(serverId, userId)
            .map { mapper.toDto(it) }
    }

    fun getCharacters(serverId: Long): Flux<CharacterDTO> {
        return repository.findByServerIdOrderByName(serverId)
            .map { mapper.toDto(it) }
    }

    fun getBySlug(serverId: Long, slug: String): Mono<CharacterDTO> {
        return repository.findFirstByServerIdAndSlug(serverId, slug)
            .map { mapper.toDto(it) }
            .switchIfEmpty(
                Mono.error {
                    IllegalArgumentException("character with slug $slug not found")
                }
            )
    }

    fun getByIds(ids: Flux<Long>): Flux<CharacterDTO> {
        return repository.findAllById(ids).map { mapper.toDto(it) }
    }
}