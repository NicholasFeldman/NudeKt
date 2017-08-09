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

        for (pixel in this) {
            var inPoly = true

            for ((i, vert) in vertices.withIndex()) {
                val p2 = vert
                val p3 = vertices[i + 1]

                val n = pixel.x * (p2.y - p3.y) + p2.x * (p3.y - pixel.y) + p3.x * (pixel.y - p2.y)
                if (n < 0) {
                    inPoly = false
                    break
                }
            }

            if (inPoly && pixel.isSkin) {
                skin++
            }
            total++
        }
        return skin.toFloat() / total.toFloat()
    }

    fun averageIntensity() = map { it.v }.sum() / size
}
