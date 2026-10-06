package pl.kozaps.aurelvio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform