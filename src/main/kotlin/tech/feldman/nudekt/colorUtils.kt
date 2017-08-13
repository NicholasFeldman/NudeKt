package tech.feldman.nudekt

fun maxRgb(r: Float, g: Float, b: Float): Float {
    return Math.max(Math.max(r, g), b)
}

fun minRgb(r: Float, g: Float, b: Float): Float {
    return Math.min(Math.min(r, g), b)
}

data class NormalizedRGB(val r: Float, val g: Float, val b: Float)
fun toNormalizedRgb(r: Float, g: Float, b: Float): NormalizedRGB {
    val sum = r + g + b
    val nr = r / sum
    val ng = g / sum
    val nb = b / sum

    return NormalizedRGB(nr, ng, nb)
}

data class Hsv(val h: Float, val s: Float, val v: Float)
fun toHsv(r: Float, g: Float, b: Float): Hsv {
    val sum = r + g + b
    val max = maxRgb(r, g, b)
    val min = minRgb(r, g, b)
    val diff = max - min

    var h = when (max) {
        r -> (g - b) / diff
        g -> 2 + (g - r) / diff
        else -> 4 + (r - g) / diff
    }.toFloat()

    h *= 60

    if (h < 0) {
        h += 360
    }

    val s = 1 - 3.toFloat() * (min / sum)
    val v = (1 / 3.toFloat()) * max

    return Hsv(h, s, v)
}
