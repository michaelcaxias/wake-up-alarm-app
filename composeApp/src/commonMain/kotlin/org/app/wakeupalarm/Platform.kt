package org.app.wakeupalarm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform