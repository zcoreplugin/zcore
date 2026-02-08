package me.zavdav.zcore.player

import org.bukkit.entity.Player

data class TeleportRequest(val source: Player, val here: Boolean, val ignore: Boolean)