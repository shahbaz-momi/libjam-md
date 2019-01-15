package com.asdev.libjam.md.tests;

import com.asdev.libjam.md.base.RootView;
import com.asdev.libjam.md.drawable.ColorDrawable;
import com.asdev.libjam.md.layout.GenericParamList;
import com.asdev.libjam.md.layout.LinearLayout;
import com.asdev.libjam.md.layout.RelativeLayout;
import com.asdev.libjam.md.util.FloatDim;
import com.asdev.libjam.md.view.View;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Asdev on 10/09/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 * <p>
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */
public class LinearLayoutTest {

    public static void main(String[] args) throws IOException {
        LinearLayout l = new LinearLayout();
        l.setOrientation(0);

        // l.setBackground(new CompoundDrawable(new ColorDrawable(Color.GRAY), new ImageDrawable(ImageIO.read(new File("assets/roadtrip.jpg")), 1)));

        View v = new View();
        GenericParamList list = new GenericParamList();
        list.putParam("gravity", 4);
        v.applyParameters(list);

        // just use this as a spacer view
        v.setBackground(null);

        View v2 = new View();
        GenericParamList list2 = new GenericParamList();
        list2.putParam("gravity", 3);
        v2.applyParameters(list2);

        v2.setMaxSize(new FloatDim(100f, 110f));
        v2.setBackground(new ColorDrawable(Color.BLUE));

        View v3 = new View();
        GenericParamList list3 = new GenericParamList();
        list3.putParam("gravity", 4);
        v3.applyParameters(list3);

        v3.setMaxSize(new FloatDim(-2f, 110f));

        v3.setBackground(new ColorDrawable(Color.RED));

        View v4 = new View();
        GenericParamList list4 = new GenericParamList();
        list4.putParam("gravity", 5);
        v4.applyParameters(list4);

        v4.setMaxSize(new FloatDim(100f, -2f));
        v4.setBackground(new ColorDrawable(Color.GREEN));

        RelativeLayout layout = new RelativeLayout();
        layout.setBackground(new ColorDrawable(Color.PINK));

        View lC = new View();
        GenericParamList list5 = new GenericParamList();
        list5.putParam("gravity", 2);
        lC.applyParameters(list5);
        lC.setBackground(new ColorDrawable(Color.CYAN));

        View lC2 = new View();
        GenericParamList list6 = new GenericParamList();
        list6.putParam("gravity", 0);
        lC2.applyParameters(list6);
        lC2.setBackground(new ColorDrawable(Color.ORANGE));

        View lC3 = new View();
        GenericParamList list7 = new GenericParamList();
        list7.putParam("gravity", 4);
        lC3.applyParameters(list7);

//        lC3.setBackground(
//                new CompoundDrawable(
//                        new ImageDrawable(ImageIO.read(new File("assets/texture.png")), ImageDrawable.SCALE_TYPE_COVER),
//                        new ImageDrawable(ImageIO.read(new File("assets/port.png")), ImageDrawable.SCALE_TYPE_CONTAIN)
//                )
//        );

        layout.addChild(lC);
        layout.addChild(lC2);

        l.addChild(v2);
        l.addChild(v);
        l.addChild(v4);
        l.addChild(v3);
        l.addChild(layout);
        l.addChild(lC3);

        RootView rootView = new RootView("Linearlayout", new Dimension(500, 500), l, false, false);
        rootView.showFrame();
    }
}
