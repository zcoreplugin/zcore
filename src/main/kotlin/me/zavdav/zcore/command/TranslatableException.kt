package me.zavdav.zcore.command

class TranslatableException(val key: String, vararg val args: Any) : RuntimeException()