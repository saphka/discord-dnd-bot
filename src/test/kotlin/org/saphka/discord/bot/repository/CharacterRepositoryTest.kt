package org.saphka.discord.bot.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.saphka.discord.bot.domain.Character
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import reactor.test.StepVerifier

@DataR2dbcTest
class CharacterRepositoryTest {

    private val serverId1: Long = 1
    private val serverId2: Long = 2
    private val ownerId1: Long = 3
    private val ownerId2: Long = 4

    @Autowired
    lateinit var repository: CharacterRepository

    @Test
    fun `User characters`() {
        val char1 = createCharacter(serverId1, ownerId1)
        val char2 = createCharacter(serverId2, ownerId1)
        val char3 = createCharacter(serverId1, ownerId2)

        StepVerifier.create(
            repository.deleteAll().thenMany(
                repository.saveAll(listOf(char1, char2, char3))
            ).thenMany(
                repository.findByServerIdAndOwnerIdOrderByName(serverId1, ownerId1)
            )
        ).assertNext {
            assertThat(it.serverId).isEqualTo(serverId1)
            assertThat(it.ownerId).isEqualTo(ownerId1)
            assertThat(it.slug).isEqualTo("slug_${serverId1}_${ownerId1}")
            assertThat(it.name).isEqualTo("name_${serverId1}_${ownerId1}")
        }.verifyComplete()

    }

    private fun createCharacter(serverId: Long, ownerId: Long) = Character(
        id = null,
        serverId = serverId,
        ownerId = ownerId,
        slug = "slug_${serverId}_${ownerId}",
        name = "name_${serverId}_${ownerId}",
        sheet = "https://shhet.com/${serverId}_${ownerId}",
        avatar = "https://avatar.com/${serverId}_${ownerId}",
    )


}