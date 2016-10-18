package com.asdev.libjam.md.util

import java.awt.Color

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