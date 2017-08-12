package tech.feldman.nudekt.region

class Regions : ArrayList<Region>() {

    fun totalPixels() = this.sumBy { it.size }

    fun averageIntensity() = map { it.averageIntensity() }.sum() / size
}
