package com.asdev.libjam.md.util

import java.awt.Color
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.util.*

/**
 * Created by Asdev on 10/16/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.util
 */

/**
 * Calculates the perceived brightness of a color.
 */
fun perceivedBrightness(c: Color) = (c.red / 255.0) * 0.299 + (c.green / 255.0) * 0.587 + (c.blue / 255.0) * 0.114

/**
 * Returns a copy of the given [BufferedImage].
 */
fun copyImage(bi: BufferedImage) = BufferedImage(bi.colorModel, bi.copyData(null), bi.colorModel.isAlphaPremultiplied, null)

/**
 * Creates a blank [BufferedImage] that is compatible with the graphics environment.
 */
fun createCompatibleImage(width: Int, height: Int) = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.createCompatibleImage(width, height, java.awt.Transparency.TRANSLUCENT)

fun generateRandomId() = UUID.randomUUID().toString()