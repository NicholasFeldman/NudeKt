package tech.feldman.nudekt

import java.awt.image.BufferedImage

internal class Detector(val image: BufferedImage) {

    val totalPixels = image.width * image.height

    val mergeRegions = mutableListOf<MutableList<Int>>()
    val skinRegions = Regions()

    var lastFrom = -1
    var lastTo = -1

    var result = false

    fun parse(): Boolean {
        return false
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

        if (fromIndex != -1 && toIndex != -1) {
            if (fromIndex != toIndex) {
                val fromRegion = mergeRegions[fromIndex]
                val toRegion = mergeRegions[toIndex]
                val region = mutableListOf<Int>()
                region.addAll(fromRegion)
                region.addAll(toRegion)
                mergeRegions[fromIndex] = region
                mergeRegions[toIndex].addAll(mergeRegions[toIndex+1])
            }
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
            result = false
            return result
        }

        skinRegions.sortBy { it.size }
        skinRegions.reverse()

        val totalSkinPixels = skinRegions.totalPixels()

        val totalSkinPercentage = totalSkinPixels / totalPixels

        // Check if there is more than 15% skin in the image
        if (totalSkinPercentage < 15) {
            result = false
            return false
        }

        // Check if the largest skin region is less than 35%
        // and the second largest region is less than 30%
        // and the third largest region is less than 30%
        val biggestRegionPercentage = skinRegions[0].size / totalPixels * 100
        val secondBiggestRegionPercentage = skinRegions[1].size / totalPixels * 100
        val thirdBiggestRegionPercentage = skinRegions[2].size / totalPixels * 100
        if (biggestRegionPercentage < 35 && secondBiggestRegionPercentage < 30 && thirdBiggestRegionPercentage < 30) {
            result = false
            return false
        }

        // Check if the largest skin region is less than 45%
        if (biggestRegionPercentage < 45) {
            result = false
            return false
        }

        // Check if the total skin is less than 30%
        // and the number of skin within the bounding polygon is less than 55% the size
        if (totalSkinPercentage < 30) {
            skinRegions.forEachIndexed { index, region ->
                val skinRate = region.skinRateInBoundingPolygon()
                if (skinRate < 0.55) {
                    result = false
                    return false
                }
            }
        }

        // Check if there are more than 60 skin regions
        // and the average intensity within the polygon is less than 0.25
        val averageIntensity = skinRegions.regionsAverageIntensity()
        if (skinRegions.size > 60 && averageIntensity < 0.25) {
            result = false
            return false
        }

        result = true
        return true
    }
}
