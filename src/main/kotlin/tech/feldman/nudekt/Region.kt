package tech.feldman.nudekt

data class Pixel(
        val id: Int,
        val isSkin: Boolean,
        val region: Int,
        val x: Int,
        val y: Int,
        val checked: Boolean,
        val v: Float
)

typealias Region = List<Pixel>

fun Region.leftMost(): Pixel {
    var minX = Integer.MAX_VALUE
    var index = 0

    this.forEachIndexed { i, pixel ->
        if (pixel.x < minX) {
            minX = pixel.x
            index = i
        }
    }

    return this[index]
}

fun Region.rightMost(): Pixel {
    var maxX = Integer.MIN_VALUE
    var index = 0

    this.forEachIndexed { i, pixel ->
        if (pixel.x > maxX) {
            maxX = pixel.x
            index = i
        }
    }

    return this[index]
}

fun Region.upperMost(): Pixel {
    var minY = Integer.MAX_VALUE
    var index = 0

    this.forEachIndexed { i, pixel ->
        if (pixel.y < minY) {
            minY = pixel.y
            index = i
        }
    }

    return this[index]
}

fun Region.lowerMost(): Pixel {
    var maxY = Integer.MIN_VALUE
    var index = 0

    this.forEachIndexed { i, pixel ->
        if (pixel.y > maxY) {
            maxY = pixel.y
            index = i
        }
    }

    return this[index]
}

fun Region.skinRateInBoundingPolygon(): Float {
    val vertices = listOf<Pixel>(leftMost(), upperMost(), rightMost(), lowerMost(), leftMost())
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

fun Region.averageIntensity(): Float {
    var totalIntensity = 0F

    this.forEach { pixel ->
        totalIntensity += pixel.v
    }

    return totalIntensity / this.size
}

typealias Regions = List<Region>

fun Regions.totalPixels(): Int {
    var totalSkin = 0

    this.forEach { pixels ->
        totalSkin += pixels.size
    }

    return totalSkin
}

fun Regions.regionsAverageIntensity(): Float {
    var totalIntensity = 0F

    this.forEach { region ->
        totalIntensity += region.averageIntensity()
    }

    return totalIntensity / this.size
}
