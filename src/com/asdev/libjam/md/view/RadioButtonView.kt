package com.asdev.libjam.md.view

import com.asdev.libjam.md.animation.FloatValueAnimator
import com.asdev.libjam.md.animation.LinearInterpolator
import com.asdev.libjam.md.layout.GenericParamList
import com.asdev.libjam.md.layout.LinearLayout
import com.asdev.libjam.md.layout.ORIENTATION_VERTICAL
import com.asdev.libjam.md.theme.*
import com.asdev.libjam.md.util.generateRandomId
import com.asdev.libjam.md.xml.XMLParamList
import res.R
import java.awt.Color
import java.awt.Cursor
import java.awt.Graphics2D

/**
 * Created by Asdev on 04/24/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 *
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.view
 */

/**
 * The size of the radio button itself.
 */
private const val RADIO_BUTTON_SIZE = 15

/**
 * The padding for the contents inside the radio button.
 */
private const val CONTENTS_PAD = 2

/**
 * The maximum padding around the radio button.
 */
private const val PADDING = 14f

/**
 * A list containing the global radio button groups.
 */
private val groups = ArrayList<RadioButtonGroup>()

/**
 * The default radio button group.
 */
private val DEFAULT_GROUP = RadioButtonGroup("DefaultRadios")

/**
 * A class that represents a radio button group.
 */
data class RadioButtonGroup(val id: String = generateRandomId(), val onChangeListener: ((RadioButtonView) -> Unit)? = null) {

    init {
        // add it self to the global list
        groups.add(this)
    }

    companion object {

        /**
         * Creates a new RadioButtonGroup or returns any existing group with the same id. Equivalent to simplying constructing a new group object.
         */
        fun createGroup(id: String, onChangeListener: ((RadioButtonView) -> Unit)? = null): RadioButtonGroup {
            val existing = getGroup(id)
            if(existing != null)
                return existing

            val group = RadioButtonGroup(id, onChangeListener)
            groups.add(group)
            return group
        }

        /**
         * Attempts to find any RadioButtonGroup with the given id, otherwise returns null.
         */
        fun getGroup(id: String): RadioButtonGroup? {
            return groups.find { it.id == id }
        }

    }

    private val buttons = ArrayList<RadioButtonView>()

    /**
     * Registers the given radio button with this group.
     */
    fun registerRadioButton(v: RadioButtonView) {
        buttons.add(v)

        // force the default state to false
        v.setChecked(false)
    }

    /**
     * Unregisters the given radio button from this group.
     */
    fun unregisterRadioButton(v: RadioButtonView) {
        buttons.remove(v)

        v.setChecked(false)
    }

    /**
     * Called when the selected view changes.
     */
    fun onSelectionChanged(newSelection: RadioButtonView) {
        // set all the other ones to false
        for(b in buttons)
            if(newSelection != b)
                b.setChecked(false)

        // call the listener
        onChangeListener?.invoke(newSelection)
    }

    /**
     * Returns the selected radio button, or null if none is selected.
     */
    fun getSelectedView(): RadioButtonView? {
        for(b in buttons)
            if(b.isChecked())
                return b

        return null
    }

    /**
     * Returns the selected group value, or null if no value is selected.
     */
    fun getSelectedValue(): String? {
        return getSelectedView()?.groupValue
    }
}

/**
 * A pressable radio button input view featuring material animations.
 */
class RadioButtonView(private val onChangeListener: ((RadioButtonView, Boolean) -> Unit)? = null,
                      /**
                       * The value of this radio button inside its parent group.
                       */
                      var groupValue: String = generateRandomId(),
                      /**
                       * The group that will house this radio button.
                       */
                      private var group: RadioButtonGroup): View() {

    constructor(): this(null, group = DEFAULT_GROUP) // required for XML inflation

    companion object {

        /**
         * Creates a radio button with a neighbouring label and the specified value.
         */
        fun makeWithLabel(label: String, groupValue: String = generateRandomId(), group: RadioButtonGroup, onChangeListener: ((RadioButtonView, Boolean) -> Unit)? = null): View {
            // make the actual cb view
            val cb = RadioButtonView(onChangeListener, groupValue, group)
            val textView = TextView(label)
            // gravity to the middle left
            textView.gravity = R.gravity.middle_left
            // put in a containing linear layout
            val container = LinearLayout()
            container.setOrientation(ORIENTATION_VERTICAL)
            container.addChild(cb)
            container.addChild(textView)

            container.onStateChangeListener = { prev, next -> cb.onStateChanged(prev, next) }
            // default constraints
            container.maxSize.w = 1000000f
            container.maxSize.h = 50f

            return container
        }

    }

    /**
     * Whether or not this radio button is checked.
     */
    private var checked = false

    /**
     * The color of the radio button when it is unactivated (false).
     */
    var colorUnactivated = R.theme.secondary_text

    /**
     * The color of the radio button when it is activated (true).
     */
    var colorActivated = R.theme.accent

    /**
     * The color of the background within this radio button.
     */
    var colorBackgroundInternal = R.theme.background

    /**
     * The animator bound with this radio button.
     */
    private val animator = FloatValueAnimator(300f, LinearInterpolator, 0f, 0f, 1f).apply { setAssignedValue(0f) }

    init {
        // by default, the minimum size should be at least the radio button size
        minSize.w = RADIO_BUTTON_SIZE.toFloat()
        minSize.h = RADIO_BUTTON_SIZE.toFloat()

        maxSize = minSize.copy()
        maxSize.w += PADDING
        maxSize.h += PADDING

        animator.setAssignedValue(0f)

        group.registerRadioButton(this)
    }

    override fun applyParameters(params: GenericParamList) {
        super.applyParameters(params)

        if(params is XMLParamList) {
            if(params.hasParam(R.attrs.RadioButtonView.color_activated)) {
                colorActivated = params.getColor(R.attrs.RadioButtonView.color_activated)!!
                setThemeColorActivated(-1)
            }

            if(params.hasParam(R.attrs.RadioButtonView.color_unactivated)) {
                colorUnactivated = params.getColor(R.attrs.RadioButtonView.color_unactivated)!!
                setThemeColorUnactivated(-1)
            }

            if(params.hasParam(R.attrs.RadioButtonView.color_background_internal)) {
                colorBackgroundInternal = params.getColor(R.attrs.RadioButtonView.color_background_internal)!!
                setThemeColorBackgroundInternal(-1)
            }

            params.setToString(R.attrs.RadioButtonView.group_value, this::groupValue)

            if(params.hasParam(R.attrs.RadioButtonView.group)) {
                val newGroupId = params.getString(R.attrs.RadioButtonView.group)!!
                // unregister from the current group
                group.unregisterRadioButton(this)

                val instance = RadioButtonGroup.createGroup(newGroupId, null)
                instance.registerRadioButton(this)
                this.group = instance
            }
        }
    }

    override fun loop() {
        super.loop()

        animator.loop()

        if(animator.isRunning()) {
            requestRepaint()
        }
    }

    override fun onStateChanged(previous: State, newState: State) {
        super.onStateChanged(previous, newState)

        if(newState == State.STATE_NORMAL) {
            setCursor(Cursor.DEFAULT_CURSOR)
        } else {
            setCursor(Cursor.HAND_CURSOR)
        }

        // if the previous was pressed and this is post press, then toggle the checked
        if(previous == State.STATE_PRESSED && newState == State.STATE_POST_PRESS && !checked) {
            setChecked(true)
        }
    }

    private var themeColorUnactiviated = COLOR_SECONDARY_TEXT
    private var themeColorActivated = COLOR_ACCENT
    private var themeColorBackgroundInternal = COLOR_BACKGROUND

    /**
     * Returns whether or not this radio button is checked.
     */
    fun isChecked() = checked

    /**
     * Sets the checked state of this radio button and begins any necessary animations.
     */
    fun setChecked(b: Boolean) {
        if(checked == b)
            return

        // if it wasn't checked before and now it is, call selection change
        if(!checked && b) {
            group.onSelectionChanged(this)
        }

        checked = b

        if(checked) {
            animator.setFromValue(0f).setToValue(1f).start()
        } else {
            animator.setFromValue(1f).setToValue(0f).start()
        }

        requestRepaint()

        onChangeListener?.invoke(this, checked)
    }

    /**
     * Sets the unactivated color of this radio button to the specified theme color.
     */
    fun setThemeColorUnactivated(color: Int) {
        themeColorUnactiviated = color

        if(themeColorUnactiviated != -1)
            colorUnactivated = THEME.getColor(themeColorUnactiviated)
    }

    /**
     * Sets the activated color of this radio button to the specified theme color.
     */
    fun setThemeColorActivated(color: Int) {
        themeColorActivated = color

        if(themeColorActivated != -1)
            colorActivated = THEME.getColor(themeColorActivated)
    }

    /**
     * Sets the checkmark color of this radio button to the specified theme color.
     */
    fun setThemeColorBackgroundInternal(color: Int) {
        themeColorBackgroundInternal = color

        if(themeColorBackgroundInternal != -1)
            colorBackgroundInternal = THEME.getColor(themeColorBackgroundInternal)
    }

    override fun onThemeChange(prevTheme: Theme, newTheme: Theme) {
        super.onThemeChange(prevTheme, newTheme)

        setThemeColorActivated(themeColorActivated)
        setThemeColorUnactivated(themeColorUnactiviated)
        setThemeColorBackgroundInternal(themeColorBackgroundInternal)
    }

    override fun onDraw(g: Graphics2D) {
        super.onDraw(g)

        // center this within the actual size
        val x = (layoutSize.w - RADIO_BUTTON_SIZE) / 2.0
        val y = (layoutSize.h - RADIO_BUTTON_SIZE) / 2.0

        g.translate(x, y)

        var prog = animator.getValue() * 2f
        var invProg = 1f - prog

        var secondProg = 0f
        var invSecondProg = 1f

        if(prog > 1f) {
            secondProg = prog - 1f
            invSecondProg = 1f - secondProg

            prog = 1f
            invProg = 0f
        }

        // draw the outside box
        g.color = Color(
                lerp(colorUnactivated.red, colorActivated.red, prog),
                lerp(colorUnactivated.green, colorActivated.green, prog),
                lerp(colorUnactivated.blue, colorActivated.blue, prog))

        // draw the outside
        g.fillOval(x.toInt() - RADIO_BUTTON_SIZE / 2, y.toInt() - RADIO_BUTTON_SIZE / 2, RADIO_BUTTON_SIZE, RADIO_BUTTON_SIZE)

        // fill the inner box
        g.color = colorBackgroundInternal
        val animSize = ((RADIO_BUTTON_SIZE - CONTENTS_PAD * 2) * invProg).toInt()
        val animPos = ((RADIO_BUTTON_SIZE - animSize) / 2f).toInt()
        g.fillOval(animPos, animPos, animSize, animSize)

        if(secondProg > 0f) {
            g.color = colorBackgroundInternal
            val animSize3 = ((RADIO_BUTTON_SIZE - CONTENTS_PAD * 2))
            val animPos3 = ((RADIO_BUTTON_SIZE - animSize3) / 2f).toInt()
            g.fillOval(animPos3, animPos3, animSize3, animSize3)

            g.color = colorActivated
            val animSize2 = ((RADIO_BUTTON_SIZE) * maxOf(invSecondProg, 0.5f)).toInt()
            val animPos2 = ((RADIO_BUTTON_SIZE - animSize2) / 2f).toInt()
            g.fillOval(animPos2, animPos2, animSize2, animSize2)
        }

        g.translate(-x, -y)

    }

}

/**
 * Linear interpolates the given values.
 */
private fun lerp(start: Int, end: Int, progress: Float) = (start + (end - start) * progress).toInt()