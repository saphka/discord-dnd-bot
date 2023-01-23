package org.saphka.discord.bot.command

sealed class FieldName {
    companion object {
        const val CHARACTER_SLUG = "slug"
        const val CHARACTER_NAME = "name"
        const val CHARACTER_SHEET_URL = "sheet"
        const val CHARACTER_AVATAR_URL = "avatar"
        const val CHARACTER_SLUG_REF = "character-slug"

        const val GAME_SLUG = "slug"
        const val GAME_NAME = "name"
        const val GAME_DATE = "date"
        const val GAME_TIER = "tier"
        const val GAME_SLUG_REF = "game-slug"

        const val GAME_LOG_TEXT = "text"
    }
}
