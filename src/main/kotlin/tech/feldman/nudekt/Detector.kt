package tech.feldman.nudekt

import java.awt.image.BufferedImage

internal class Detector(val image: BufferedImage) {

    val totalPixels = image.width * image.height

    fun parse(): Boolean {
        return false
    }
}
