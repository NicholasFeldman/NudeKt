package tech.feldman.nudekt

import java.awt.image.BufferedImage

internal class Detector(val image: BufferedImage) {

    val totalPixels = image.width * image.height

    val mergeRegions = mutableListOf<MutableList<Int>>()
    val skinRegions = Regions()

    var lastFrom = -1
    var lastTo = -1

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
}
