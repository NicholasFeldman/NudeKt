package tech.feldman.nudekt.region

data class Pixel(
        val id: Int,
        val isSkin: Boolean,
        var region: Int,
        val x: Int,
        val y: Int,
        val checked: Boolean,
        val v: Float
)
