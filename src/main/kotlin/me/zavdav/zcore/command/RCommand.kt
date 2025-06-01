package me.zavdav.zcore.command

internal val rCommand = command(
    "r",
    arrayOf("reply"),
    "A shorthand for /msg to reply to the last player.",
    "/r <message>",
    "zcore.r"
) {
    stringArgument("message", StringType.GREEDY_STRING) {
        runs(permission) {
            val source = requirePlayer()
            val message: String by this
            val replyingTo = source.replyingTo

            if (replyingTo == null || !replyingTo.isOnline) {
                throw TranslatableException("command.r.noOne")
            }

            source.privateMessage(replyingTo, message)
        }
    }
}