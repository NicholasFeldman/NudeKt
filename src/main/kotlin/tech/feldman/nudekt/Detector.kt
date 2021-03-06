package tech.feldman.nudekt

import tech.feldman.nudekt.region.Pixel
import tech.feldman.nudekt.region.Region
import tech.feldman.nudekt.region.Regions
import java.awt.Color
import java.awt.image.BufferedImage

internal class Detector(val image: BufferedImage) {

    val totalPixels = image.width * image.height

    val mergeRegions = mutableListOf<MutableList<Int>>()
    val skinRegions = Regions()
    val detectedRegions = Regions()

    var lastFrom = -1
    var lastTo = -1

    var pixels = Region()

    fun parse(): Boolean {
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = Color(image.getRGB(x, y))
                val normR = color.red / 256.toFloat()
                val normG = color.green / 256.toFloat()
                val normB = color.blue / 256.toFloat()

                val currentIndex = x + y * image.width
                val nextIndex = currentIndex + 1

                val isSkin = classifySkin(normR, normG, normB)
                val (_, _, v) = toHsv(normR, normG, normB)

                if (!isSkin) {
                    pixels.add(Pixel(currentIndex, false, 0, x, y, false, v))
                } else {
                    pixels.add(Pixel(currentIndex, true, 0, x, y, false, v))

                    var region = -1
                    val checkIndexes = listOf(
                            nextIndex - 2,
                            nextIndex - image.width - 2,
                            nextIndex - image.width - 1,
                            nextIndex - image.width
                    )
                    var checker = false

                    for (checkIndex in checkIndexes) {
                        if (checkIndex < 0) {
                            continue
                        }
                        val skin = pixels[checkIndex]
                        if (skin.isSkin) {
                            if (skin.region != region &&
                                    region != -1 &&
                                    lastFrom != region &&
                                    lastTo != skin.region) {
                                addMerge(region, skin.region)
                            }
                            region = pixels[checkIndex].region
                            checker = true
                        }
                    }

                    if (!checker) {
                        pixels[currentIndex].region = detectedRegions.size
                        val newRegion = Region()
                        newRegion.add(pixels[currentIndex])
                        detectedRegions.add(newRegion)
                        continue
                    } else {
                        if (region > -1) {
                            if (detectedRegions.size >= region) {
                                detectedRegions.add(Region())
                            }
                            pixels[currentIndex].region = region
                            detectedRegions[region].add(pixels[currentIndex])
                        }
                    }
                }

            }
        }

        merge(detectedRegions, mergeRegions)
        return analyzeRegions()
    }

    fun addMerge(from: Int, to: Int) {
        lastFrom = from
        lastTo = to

        var fromIndex = -1
        var toIndex = -1

        mergeRegions.forEachIndexed { index, region ->
            region.forEach { regionIndex ->
                if (regionIndex == from) fromIndex = index
                if (regionIndex == to) toIndex = index
            }
        }

        if (fromIndex != -1 && toIndex != -1 && fromIndex == toIndex) {
            return
        }

        if (fromIndex == -1 && toIndex == -1) {
            mergeRegions.add(mutableListOf(from, to))
            return
        }

        if (fromIndex != -1 && toIndex == -1) {
            mergeRegions[fromIndex].add(to)
        }

        if (fromIndex == -1 && toIndex != -1) {
            mergeRegions[toIndex].add(from)
        }

        if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
            mergeRegions[fromIndex].addAll(mergeRegions[toIndex])
            mergeRegions.removeAt(toIndex)
        }
    }

    fun merge(detectedRegions: Regions, mergeRegions: List<List<Int>>) {
        val newDetectedRegions = Regions()

        mergeRegions.forEachIndexed { index, region ->
            if (newDetectedRegions.size >= index) {
                newDetectedRegions.add(Region())
            }
            region.forEach { r ->
                newDetectedRegions[index].addAll(detectedRegions[r])
                detectedRegions[r] = Region()
            }
        }

        detectedRegions.forEach { region ->
            if (region.size > 0) {
                newDetectedRegions.add(region)
            }
        }

        clearRegions(newDetectedRegions)
    }

    fun clearRegions(detectedRegions: Regions) {
        detectedRegions.forEach { region ->
            if (region.size > 30) {
                skinRegions.add(region)
            }
        }
    }

    fun analyzeRegions(): Boolean {
        if (skinRegions.size < 3) {
            return false
        }

        skinRegions.sortBy { it.size }
        skinRegions.reverse()

        val totalSkinPixels = skinRegions.totalPixels().toFloat()

        val totalSkinPercentage: Float = totalSkinPixels / totalPixels * 100

        // Check if there is more than 15% skin in the image
        if (totalSkinPercentage < 15) {
            return false
        }

        // Check if the largest skin region is less than 35%
        // and the second largest region is less than 30%
        // and the third largest region is less than 30%
        val biggestRegionPercentage: Float = skinRegions[0].size.toFloat() / totalSkinPixels * 100
        val secondBiggestRegionPercentage: Float = skinRegions[1].size.toFloat() / totalSkinPixels * 100
        val thirdBiggestRegionPercentage: Float = skinRegions[2].size.toFloat() / totalSkinPixels * 100
        if (biggestRegionPercentage < 35 && secondBiggestRegionPercentage < 30 && thirdBiggestRegionPercentage < 30) {
            return false
        }

        // Check if the largest skin region is less than 45%
        if (biggestRegionPercentage < 45) {
            return false
        }

        // Check if the total skin is less than 30%
        // and the number of skin within the bounding polygon is less than 55% the size
        if (totalSkinPercentage < 30) {
            skinRegions.forEach { region ->
                val skinRate = region.skinRateInBoundingPolygon()
                if (skinRate < 0.55) {
                    return false
                }
            }
        }

        // Check if there are more than 60 skin regions
        // and the average intensity within the polygon is less than 0.25
        val averageIntensity = skinRegions.averageIntensity()
        if (skinRegions.size > 60 && averageIntensity < 0.25) {
            return false
        }

        return true
    }
}

internal fun classifySkin(r: Float, g: Float, b: Float): Boolean {
    val rgbClassifier = r > 95 &&
            g > 40 && g < 100 &&
            b > 20 &&
            maxRgb(r, g, b) - minRgb(r, g, b) > 15 &&
            Math.abs(r - g) > 15 &&
            r > g &&
            r > b

    val (nr, ng, _) = toNormalizedRgb(r, g, b)
    val normalizedRgbClassifier = nr / ng > 1.185 &&
            (r * b) / Math.pow((r + g + b).toDouble(), 2.toDouble()) > 0.107 &&
            (r * g) / Math.pow((r + g + b).toDouble(), 2.toDouble()) > 0.112

    val (h, s, _) = toHsv(r, g, b)
    val hsvClassifier = h > 0 &&
            h < 35 &&
            s > 0.23 &&
            s < 0.68

    return rgbClassifier || normalizedRgbClassifier || hsvClassifier
}
