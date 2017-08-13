package tech.feldman.nudekt.extensions

import tech.feldman.nudekt.region.Pixel
import java.awt.Polygon

internal fun Polygon.contains(pixel: Pixel) = this.contains(pixel.x, pixel.y)

// Extension "constructor" that allows us to initialize a polygon with all of our pixel points
internal fun Polygon(vararg pixels: Pixel) = Polygon().apply { pixels.forEach { this.addPoint(it.x, it.y) } }
