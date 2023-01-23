package org.saphka.discord.bot.command

sealed class CommandName {
    companion object {
        const val ADMIN_COMMAND = "admin"
        const val ADMIN_CREATE_GAME = "create-game"
        const val ADMIN_LIST_CHARACTERS = "list-all-characters"
        const val ADMIN_LIST_ENROLLED = "list-enrolled"
        const val ADMIN_LOG_ADD = "log-add"
        const val ADMIN_LOG_LIST = "log-list"

        const val CHARACTER_COMMAND = "character"
        const val CHARACTER_CREATE = "create"
        const val CHARACTER_LIST = "list"

        const val GAME_COMMAND = "game"
        const val GAME_LIST = "list"
        const val GAME_ENROLL = "enroll"
        const val GAME_UN_ENROLL = "un-enroll"
    }
}

