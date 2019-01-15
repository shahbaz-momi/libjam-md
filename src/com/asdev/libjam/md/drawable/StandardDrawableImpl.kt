package com.asdev.libjam.md.drawable

import com.asdev.libjam.md.util.*
import com.asdev.libjam.md.view.View
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.drawable
 *
 */

/**
 * A standard drawable that draws a solid color.
 */
class ColorDrawable(c: Color) : Drawable() {

    var color: Color = c

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        g.color = color
        g.fillRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

}

/**
 * Creates a new ImageDrawable from the given file at the path.
 */
fun newImageDrawable(path: String): ImageDrawable {
    val img = ImageIO.read(File(path))
    return ImageDrawable(img)
}

class ImageDrawable: Drawable {

    companion object {
        const val SCALE_TYPE_CONTAIN = 0
        const val SCALE_TYPE_COVER = 1
        const val SCALE_TYPE_FIT = 2
        const val SCALE_TYPE_ORIGINAL = 3
    }

    val img: Image
    var scaleType: Int = SCALE_TYPE_CONTAIN

    constructor(img: Image) {
        this.img = img
        scaleType = SCALE_TYPE_CONTAIN
    }

    constructor(img: Image, scaleType: Int) {
        this.img = img
        this.scaleType = scaleType
    }

    /**
     * Returns a clone of this ImageDrawable without cloning the underlying image.
     */
    fun softClone() = ImageDrawable(img, scaleType)

    /**
     * Returns a clone of this ImageDrawable without cloning the underlying image using the given scale type.
     */
    fun softClone(scaleType: Int) = ImageDrawable(img, scaleType)

    /**
     * Draws the foreground to this drawable according to the scale type.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        val clipP = g.clip

        val width = img.getWidth(null)
        val height = img.getHeight(null)

        g.clipRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
        // check scaling mode
        if(scaleType == SCALE_TYPE_ORIGINAL) {
            // center the original image size within this
            g.drawImage(img, x.toInt() + ((w - width) / 2f).toInt(), y.toInt() + ((h - height) / 2f).toInt(), width, height, null)
        } else if(scaleType == SCALE_TYPE_FIT) {
            // stretch w and h to fit the bounding size
            g.drawImage(img, x.toInt(), y.toInt(), w.toInt(), h.toInt(), null)
        } else if(scaleType == SCALE_TYPE_CONTAIN) {
            val sfh = h / height
            val newW = height * sfh

            val sfw = w / width
            val newH = width * sfw

            if(newH > h) {
                // center the foreground horizontally
                g.drawImage(img, x.toInt() + ((w - newW) / 2f).toInt(), y.toInt(), newW.toInt(), h.toInt(), null)
            } else {
                g.drawImage(img, x.toInt(), y.toInt() + ((h - newH) / 2f).toInt(), w.toInt(), newH.toInt(), null)
            }
        } else if(scaleType == SCALE_TYPE_COVER) {
            val sfh = h / height
            val newW = width * sfh

            // width is the width of the view
            val sfw = w / width
            val newH = height * sfw

            if(newH < h) {
                // center the foreground horizontally
                g.drawImage(img, x.toInt() + ((w - newW) / 2f).toInt(), y.toInt(), newW.toInt(), h.toInt(), null)
            } else {
                g.drawImage(img, x.toInt(), y.toInt() + ((h - newH) / 2f).toInt(), w.toInt(), newH.toInt(), null)
            }
        }

        // set the clip back
        g.clip = clipP
    }
}

/**
 * Turns multiple drawables into one.
 */
class CompoundDrawable(vararg private val drawables: Drawable): Drawable() {

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        for(d in drawables)
            d.draw(g, x, y, w, h)
    }

}

/**
 * Accepts a standard Android nine-patch foreground and draws it as a nine-patch drawable. NOTE: this is slow, so using caching
 * (by setting cache = true) if you are going to be constantly be repainting the foreground. This may improve performance just
 * a bit in exchange for memory.
 */
class NinePatchDrawable(private val ninepatch: BufferedImage, private val doCache: Boolean = false, private val drawMiddlePatch: Boolean = true): Drawable() {

    /**
     * The coordinates of the nine-patch elements.
     */
    private val coords = Array(9){ Point(-2, -2) }

    /**
     * The sizes of the nine-patch elements.
     */
    private val sizes = Array(9){ Dimension(-2, -2) }

    // private val section: Array<BufferedImage>

    private var cache: BufferedImage? = null

    /**
     * Calculates the coordinates of the nine-patch foreground.
     */
    init {
        // map of the array:
        // 0 1 2
        // 3 4 5
        // 6 7 8

        // find the bounds of the nine patch
        // the nine-patch adds 2 pixels to each side, and draws black in the sections
        // the top-left patch coords are always 1,1
        coords[0] = Point(1, 1)
        coords[3] = Point(1, 1)
        coords[6] = Point(1, 1)

        // set the known ys
        coords[0].y = 1
        coords[1].y = 1
        coords[2].y = 1

        // find the width of the left row
        for(x in 0 until ninepatch.width) {
            // check if it is black
            if(Color(ninepatch.getRGB(x, 0), true) == Color.BLACK) {
                // set the sizes and coords
                // the width for the entire left row
                // the x of the middle row
                sizes[0].width = x - 2
                sizes[3].width = x - 2
                sizes[6].width = x - 2
                coords[1].x = x
                coords[4].x = x
                coords[7].x = x

                if(DEBUG) {
                    println("[NinePatchDrawable] Using a left x of $x and a width for the left column of ${x - 2}")
                }

                break
            }
        }

        // find the horizontal last point
        for(x in ninepatch.width - 1 downTo 0) {
            // check if it is black
            if(Color(ninepatch.getRGB(x, 0), true) == Color.BLACK) {
                // the width for the middle row
                // the x of the right row
                sizes[1].width = (x - 1) - coords[1].x
                sizes[4].width = (x - 1) - coords[4].x
                sizes[7].width = (x - 1) - coords[7].x
                coords[2].x = x
                coords[5].x = x
                coords[8].x = x
                // the width of the last row is from x to width
                sizes[2].width = ninepatch.width - 1 - x
                sizes[5].width = ninepatch.width - 1 - x
                sizes[8].width = ninepatch.width - 1 - x

                break
            }
        }

        // find the width of the left row
        for(y in 0 until ninepatch.height) {
            // check if it is black
            if(Color(ninepatch.getRGB(0, y), true) == Color.BLACK) {
                // set the sizes and coords
                // the width for the entire left row
                // the x of the middle row
                sizes[0].height = y - 2
                sizes[1].height = y - 2
                sizes[2].height = y - 2
                coords[3].y = y
                coords[4].y = y
                coords[5].y = y

                break
            }
        }

        // find the horizontal last point
        for(y in ninepatch.height - 1 downTo 0) {
            // check if it is black
            if(Color(ninepatch.getRGB(0, y), true) == Color.BLACK) {
                // the width for the middle row
                // the x of the right row
                sizes[3].height = (y - 1) - coords[3].y
                sizes[4].height = (y - 1) - coords[4].y
                sizes[5].height = (y - 1) - coords[5].y
                coords[6].y = y
                coords[7].y = y
                coords[8].y = y
                // the width of the last row is from x to width
                sizes[6].height = ninepatch.height - 1 - y
                sizes[7].height = ninepatch.height - 1 - y
                sizes[8].height = ninepatch.height - 1 - y

                break
            }
        }

        // check if any of the findings error
        if(sizes.filter { it.width <= 0 || it.height <= 0 }.isNotEmpty() ||
                coords.filter { it.x < 0 || it.y < 0 }.isNotEmpty()) {
            // throw an illegal argument exception
            throw IllegalArgumentException("The passed NinePatch isn't a valid nine patch!")
        }

        // create a subimage for each section because cropping and drawing the images are soo slow
//        section = Array(9) { i ->
//            ninepatch.getSubimage(coords[i].x, coords[i].y, sizes[i].width, sizes[i].height)
//        }

        if(DEBUG) {
            println("[NinePatchDrawable] Coords: ${coords.joinToString(" ")}, Sizes: ${sizes.joinToString(" ")}")
        }
    }

    /**
     * Draws the nine patch to the specified bounding box.
     */
    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float) {
        // 0 1 2
        // 3 4 5
        // 6 7 8

        val prevClip = g.clip

        // clip one over to prevent wierd lines from appearing
        g.clipRect(x.toInt() + 1, y.toInt() + 1, w.toInt() - 2, h.toInt() - 2)

        // left section
        val leftW = sizes[0].width
        val rightW = sizes[2].width

        // middle section x and width
        val midW = w.toInt() - leftW - rightW

        val leftStartX = x.toInt()
        val leftEndX = leftStartX + sizes[0].width

        val midStartX = leftEndX
        val midEndX = midStartX + midW

        val rightStartX = midEndX
        val rightEndX = rightStartX + rightW

        val topH = sizes[0].height
        val bottomH = sizes[2].height

        val midH = h.toInt() - topH - bottomH

        val topStartY = y.toInt()
        val topEndY = topStartY + topH

        val midStartY = topEndY
        val midEndY = midStartY + midH

        val bottomStartY = midEndY
        val bottomEndY = bottomStartY + bottomH

        // if caching, just draw that
        if(doCache) {
            if(cache != null && cache!!.width == w.toInt() && cache!!.height == h.toInt()) {
                if(DEBUG) {
                    println("[NinePatchDrawable] Drawing from cache...")
                }
                g.drawImage(cache, x.toInt(), y.toInt(), null)
            } else {
                if(DEBUG) {
                    println("[NinePatchDrawable] Purging cache...")
                }
                // draw it to the cache
                cache = createCompatibleImage(w.toInt(), h.toInt())
                val cg = cache!!.createGraphics()!!

                val xI = x.toInt()
                val yI = y.toInt()

                // draw section 0
                cg.drawImage(ninepatch, leftStartX - xI, topStartY - yI, leftEndX- xI, topEndY - yI, coords[0].x, coords[0].y, coords[0].x + sizes[0].width, coords[0].y + sizes[0].height, null)
                // g.drawImage(section[0], leftStartX, topStartY, section[0].width, section[0].height, null)
                // draw section 1
                cg.drawImage(ninepatch, midStartX- xI, topStartY - yI, midEndX- xI, topEndY - yI, coords[1].x, coords[1].y, coords[1].x + sizes[1].width, coords[1].y + sizes[1].height, null)
                // g.drawImage(section[1], midStartX, topStartY, (midEndX - midStartX), (topEndY - topStartY), null)
                // draw section 2
                cg.drawImage(ninepatch, rightStartX- xI, topStartY - yI, rightEndX- xI, topEndY - yI, coords[2].x, coords[2].y, coords[2].x + sizes[2].width, coords[2].y + sizes[2].height, null)
                // g.drawImage(section[2], rightStartX, topStartY, (rightEndX - rightStartX), (topEndY - topStartY), null)
                // draw section 3
                cg.drawImage(ninepatch, leftStartX- xI, midStartY - yI, leftEndX- xI, midEndY - yI, coords[3].x, coords[3].y, coords[3].x + sizes[3].width, coords[3].y + sizes[3].height, null)
                // g.drawImage(section[3], leftStartX, midStartY, (leftEndX - leftStartX), (midEndY - midStartY), null)
                // draw section 4
                if(drawMiddlePatch)
                    cg.drawImage(ninepatch, midStartX- xI, midStartY - yI, midEndX- xI, midEndY - yI, coords[4].x, coords[4].y, coords[4].x + sizes[4].width, coords[4].y + sizes[4].height, null)
                // g.drawImage(section[4], midStartX, midStartY, (midEndX - midStartX), (midEndY - midStartY), null)
                // draw section 5
                cg.drawImage(ninepatch, rightStartX- xI, midStartY - yI, rightEndX- xI, midEndY - yI, coords[5].x, coords[5].y, coords[5].x + sizes[5].width, coords[5].y + sizes[5].height, null)
                // g.drawImage(section[5], rightStartX, midStartY, (rightEndX - rightStartX), (midEndY - midStartY), null)
                // draw section 6
                cg.drawImage(ninepatch, leftStartX- xI, bottomStartY - yI, leftEndX- xI, bottomEndY - yI, coords[6].x, coords[6].y, coords[6].x + sizes[6].width, coords[6].y + sizes[6].height, null)
                // g.drawImage(section[6], leftStartX, bottomStartY, (leftEndX - leftStartX), (bottomEndY - bottomStartY), null)
                // draw section 7
                cg.drawImage(ninepatch, midStartX- xI, bottomStartY - yI, midEndX- xI, bottomEndY - yI, coords[7].x, coords[7].y, coords[7].x + sizes[7].width, coords[7].y + sizes[7].height, null)
                // g.drawImage(section[7], midStartX, bottomStartY, (midEndX - midStartX), (bottomEndY - bottomStartY), null)
                // draw section 8
                cg.drawImage(ninepatch, rightStartX- xI, bottomStartY - yI, rightEndX- xI, bottomEndY - yI, coords[8].x, coords[8].y, coords[8].x + sizes[8].width, coords[8].y + sizes[8].height, null)
                // g.drawImage(section[8], rightStartX, bottomStartY, (rightEndX - rightStartX), (bottomEndY - bottomStartY), null)

                // draw the cache
                g.drawImage(cache, x.toInt(), y.toInt(), null)
            }

            g.clip = prevClip
            return
        }

        // draw section 0
        g.drawImage(ninepatch, leftStartX, topStartY, leftEndX, topEndY, coords[0].x, coords[0].y, coords[0].x + sizes[0].width, coords[0].y + sizes[0].height, null)
        // g.drawImage(section[0], leftStartX, topStartY, section[0].width, section[0].height, null)
        // draw section 1
        g.drawImage(ninepatch, midStartX, topStartY, midEndX, topEndY, coords[1].x, coords[1].y, coords[1].x + sizes[1].width, coords[1].y + sizes[1].height, null)
        // g.drawImage(section[1], midStartX, topStartY, (midEndX - midStartX), (topEndY - topStartY), null)
        // draw section 2
        g.drawImage(ninepatch, rightStartX, topStartY, rightEndX, topEndY, coords[2].x, coords[2].y, coords[2].x + sizes[2].width, coords[2].y + sizes[2].height, null)
        // g.drawImage(section[2], rightStartX, topStartY, (rightEndX - rightStartX), (topEndY - topStartY), null)
        // draw section 3
        g.drawImage(ninepatch, leftStartX, midStartY, leftEndX, midEndY, coords[3].x, coords[3].y, coords[3].x + sizes[3].width, coords[3].y + sizes[3].height, null)
        // g.drawImage(section[3], leftStartX, midStartY, (leftEndX - leftStartX), (midEndY - midStartY), null)
        // draw section 4
        if(drawMiddlePatch)
            g.drawImage(ninepatch, midStartX, midStartY, midEndX, midEndY, coords[4].x, coords[4].y, coords[4].x + sizes[4].width, coords[4].y + sizes[4].height, null)
        // g.drawImage(section[4], midStartX, midStartY, (midEndX - midStartX), (midEndY - midStartY), null)
        // draw section 5
        g.drawImage(ninepatch, rightStartX, midStartY, rightEndX, midEndY, coords[5].x, coords[5].y, coords[5].x + sizes[5].width, coords[5].y + sizes[5].height, null)
        // g.drawImage(section[5], rightStartX, midStartY, (rightEndX - rightStartX), (midEndY - midStartY), null)
        // draw section 6
        g.drawImage(ninepatch, leftStartX, bottomStartY, leftEndX, bottomEndY, coords[6].x, coords[6].y, coords[6].x + sizes[6].width, coords[6].y + sizes[6].height, null)
        // g.drawImage(section[6], leftStartX, bottomStartY, (leftEndX - leftStartX), (bottomEndY - bottomStartY), null)
        // draw section 7
        g.drawImage(ninepatch, midStartX, bottomStartY, midEndX, bottomEndY, coords[7].x, coords[7].y, coords[7].x + sizes[7].width, coords[7].y + sizes[7].height, null)
        // g.drawImage(section[7], midStartX, bottomStartY, (midEndX - midStartX), (bottomEndY - bottomStartY), null)
        // draw section 8
        g.drawImage(ninepatch, rightStartX, bottomStartY, rightEndX, bottomEndY, coords[8].x, coords[8].y, coords[8].x + sizes[8].width, coords[8].y + sizes[8].height, null)
        // g.drawImage(section[8], rightStartX, bottomStartY, (rightEndX - rightStartX), (bottomEndY - bottomStartY), null)

        g.clip = prevClip
    }

}

/**
 * Draws the given drawables for each state.
 */
class StatefulCompoundDrawable(val normal: Drawable, val hover: Drawable? = null, val pressed: Drawable? = null, val focused: Drawable? = null): StatefulDrawable() {

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State) {
        if(state == View.State.STATE_NORMAL) {
            normal.draw(g, x, y, w, h)
        } else if(state == View.State.STATE_HOVER) {
            if(hover != null)
                hover.draw(g, x, y, w, h)
            else
                normal.draw(g, x, y, w, h)
        } else if(state == View.State.STATE_POST_PRESS) {
            if(focused != null)
                focused.draw(g, x, y, w, h)
            else
                normal.draw(g, x, y, w, h)
        } else if(state == View.State.STATE_PRESSED) {
            if(pressed != null)
                pressed.draw(g, x, y, w, h)
            else
                normal.draw(g, x, y, w, h)
        }
    }

}

/**
 * A compound animated drawable that will attempt to animate its children.
 */
class AnimatedCompoundDrawable(private vararg val drawables: Drawable): AnimatedDrawable() {


    /**
     * Returns the drawables that this compound drawable manages.
     */
    fun getDrawables() = drawables

    override fun onStateChanged(bounder: View?, prevState: View.State, newState: View.State) {
        // try and update the states of the children
        for (it in drawables) {
            if(it is StatefulDrawable)
                it.onStateChanged(bounder, prevState, newState)
        }
    }

    override fun requestFrame(): Boolean {
        for (it in drawables) {
            if(it is AnimatedDrawable && it.requestFrame())
                return true
        }

        return false
    }

    override fun draw(g: Graphics2D, x: Float, y: Float, w: Float, h: Float, state: View.State, progress: Float) {
        for(d in drawables)
            if(d is AnimatedDrawable)
                d.draw(g, x, y, w, h, state, progress)
            else if(d is StatefulDrawable)
                d.draw(g, x, y, w, h, state)
            else
                d.draw(g, x, y, w, h)
    }

}