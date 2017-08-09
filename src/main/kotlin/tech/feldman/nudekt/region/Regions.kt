package tech.feldman.nudekt.region

class Regions : ArrayList<Region>() {

    fun totalPixels(): Int {
        var totalSkin = 0

        this.forEach { region ->
            totalSkin += region.size
        }

        return totalSkin
    }

    fun regionsAverageIntensity(): Float {
        var totalIntensity = 0F

        this.forEach { region ->
            totalIntensity += region.averageIntensity()
        }

        return totalIntensity / this.size
    }
}
