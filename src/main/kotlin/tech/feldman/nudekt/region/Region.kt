package tech.feldman.nudekt.region

class Region : ArrayList<Pixel>() {

    fun leftMost()  = minBy { it.x } ?: this[0]
    fun rightMost() = maxBy { it.x } ?: this[0]
    fun upperMost() = minBy { it.y } ?: this[0]
    fun lowerMost() = maxBy { it.y } ?: this[0]

    fun skinRateInBoundingPolygon(): Float {
        val vertices = listOf(leftMost(), upperMost(), rightMost(), lowerMost(), leftMost())
        var total = 0
        var skin = 0

        this.forEach { p1 ->
            var inPoly = true

            vertices.forEachIndexed { i, vert ->
                val p2 = vert
                val p3 = vertices[i + 1]

                val n = p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)
                if (n < 0) {
                    inPoly = false
                    return@forEach
                }
            }
            if (inPoly && p1.isSkin) {
                skin++
            }
            total++
        }
        return skin.toFloat() / total.toFloat()
    }

    fun averageIntensity(): Float {
        var totalIntensity = 0F

        this.forEach { pixel ->
            totalIntensity += pixel.v
        }

        return totalIntensity / this.size
    }
}
