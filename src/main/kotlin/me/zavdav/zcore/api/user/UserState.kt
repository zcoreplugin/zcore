package me.zavdav.zcore.api.user

/**
 * Represents the boolean states of a user.
 * A state has a [default] value and can persist offline, specified by [persistsOffline].
 */
enum class UserState(default: Boolean, persistsOffline: Boolean) {

    /** If the user has god mode enabled. */
    GODMODE(false, true),

    /** If the user is vanished. */
    VANISHED(false, true),

    /** If the user has SocialSpy enabled. */
    SOCIALSPY(false, true),

    /** If the user can see chat messages. */
    CHAT_ENABLED(true, true),

    /** If the user is away-from-keyboard. */
    AFK(false, false),

    /** If the user has InvSee enabled. */
    INVSEE(false, false)

}