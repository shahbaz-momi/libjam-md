package com.asdev.libjam.md.util;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * Created by Asdev on 04/20/17. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 * <p>
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.util
 *
 * Uses reflection hacks to create a mutable alpha composite.
 */
public class AlphaCompositeMutable {

    public AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f);

    private Field field;

    public AlphaCompositeMutable() {
        try {
            field = AlphaComposite.class.getDeclaredField("extraAlpha");
            field.setAccessible(true);

            setAlpha(1f);
        } catch (NoSuchFieldException ignored) { }
    }

    public void setAlpha(float a) {
        try {
            field.setFloat(composite, a);
        } catch (IllegalAccessException ignored) { }
    }

}
