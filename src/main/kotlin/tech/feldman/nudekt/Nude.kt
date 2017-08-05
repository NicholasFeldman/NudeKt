package tech.feldman.nudekt

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun isNude(path: String) = isNude(File(path))

fun isNude(file: File) = isNude(ImageIO.read(file))

fun isNude(image: BufferedImage) = Detector(image).parse()
