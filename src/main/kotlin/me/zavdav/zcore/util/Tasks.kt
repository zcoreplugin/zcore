package me.zavdav.zcore.util

import me.zavdav.zcore.ZCore
import org.bukkit.Bukkit

internal inline fun syncTask(crossinline task: () -> Unit) =
    Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.INSTANCE) { task() }

internal inline fun syncDelayedTask(delay: Long, crossinline task: () -> Unit) =
    Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.INSTANCE, { task() }, delay)

internal inline fun syncRepeatingTask(delay: Long, interval: Long, crossinline task: () -> Unit) =
    Bukkit.getScheduler().scheduleSyncRepeatingTask(ZCore.INSTANCE, { task() }, delay, interval)

internal inline fun asyncTask(crossinline task: () -> Unit) =
    Bukkit.getScheduler().scheduleAsyncDelayedTask(ZCore.INSTANCE) { task() }