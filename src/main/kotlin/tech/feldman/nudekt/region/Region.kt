package tech.feldman.nudekt.region

import tech.feldman.nudekt.extensions.Polygon
import tech.feldman.nudekt.extensions.contains

class Region : ArrayList<Pixel>() {

    fun leftMost()  = minBy { it.x } ?: this[0]
    fun rightMost() = maxBy { it.x } ?: this[0]
    fun upperMost() = minBy { it.y } ?: this[0]
    fun lowerMost() = maxBy { it.y } ?: this[0]

    fun skinRateInBoundingPolygon(): Float {
        val poly = Polygon(leftMost(), upperMost(), rightMost(), lowerMost())

        var total = 0F
        var skin = 0F

        for (pixel in this) {
            if (poly.contains(pixel) && pixel.isSkin) {
                skin++
            }
            total++
        }

        return skin / total
    }

    fun averageIntensity() = map { it.v }.sum() / size
}
