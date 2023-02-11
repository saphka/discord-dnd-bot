package org.saphka.discord.bot.service

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TimeAccessor {

    fun getCurrentTime(): LocalDateTime = LocalDateTime.now()

}
