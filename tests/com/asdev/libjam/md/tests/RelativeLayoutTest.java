package com.asdev.libjam.md.tests;

import com.asdev.libjam.md.base.RootView;
import com.asdev.libjam.md.drawable.ColorDrawable;
import com.asdev.libjam.md.layout.GenericParamList;
import com.asdev.libjam.md.layout.RelativeLayout;
import com.asdev.libjam.md.util.FloatDim;
import com.asdev.libjam.md.view.View;

import java.awt.*;

/**
 * Created by Asdev on 10/05/16. All rights reserved.
 * Unauthorized copying via any medium is stricitly
 * prohibited.
 * <p>
 * Authored by Shahbaz Momi as part of libjam-md
 * under the package com.asdev.libjam.md.tests
 */
public class RelativeLayoutTest {

    public static void main(String[] args) {
        RelativeLayout v = new RelativeLayout();
        v.setBackground(new ColorDrawable(Color.PINK));

        // -2f, -2f is the DIM_UNLIMITED
        v.setMinSize(new FloatDim(-2f, -2f));
        v.setMaxSize(new FloatDim(-2f, -2f));

        // add a child
        View child = new View();
        GenericParamList list = new GenericParamList();
        list.putParam("gravity", 0);
        child.applyParameters(list);

        child.setBackground(new ColorDrawable(Color.BLACK));

        View child2 = new View();
        GenericParamList list2 = new GenericParamList();
        list2.putParam("gravity", 1);
        child2.applyParameters(list2);

        child2.setBackground(new ColorDrawable(Color.BLUE));

        View child3 = new View();
        GenericParamList list3 = new GenericParamList();
        list3.putParam("gravity", 2);
        child3.applyParameters(list3);

        child3.setBackground(new ColorDrawable(Color.RED));

        View child4 = new View();
        GenericParamList list4 = new GenericParamList();
        list4.putParam("gravity", 3);
        child4.applyParameters(list4);

        child4.setBackground(new ColorDrawable(Color.GREEN));

        View child5 = new View();
        GenericParamList list5 = new GenericParamList();
        list5.putParam("gravity", 5);
        child5.applyParameters(list5);

        child5.setBackground(new ColorDrawable(Color.YELLOW));

        View child6 = new View();
        GenericParamList list6 = new GenericParamList();
        list6.putParam("gravity", 6);
        child6.applyParameters(list6);

        child6.setBackground(new ColorDrawable(Color.CYAN));

        RelativeLayout l2 = new RelativeLayout();
        GenericParamList l = new GenericParamList();
        l.putParam("gravity", 8);
        l2.applyParameters(l);
        l2.setBackground(new ColorDrawable(Color.DARK_GRAY));

        View child7 = new View();
        GenericParamList list7 = new GenericParamList();
        list7.putParam("gravity", 0);
        child7.applyParameters(list7);

        child7.setBackground(new ColorDrawable(Color.MAGENTA));

        View child8 = new View();
        GenericParamList list8 = new GenericParamList();
        list8.putParam("gravity", 8);
        child8.applyParameters(list8);

        child8.setBackground(new ColorDrawable(Color.ORANGE));

        l2.addChild(child7);
        l2.addChild(child8);


        v.addChild(child);
        v.addChild(child2);
        v.addChild(child3);
        v.addChild(child4);
        v.addChild(child5);
        v.addChild(child6);
        v.addChild(l2);

        RootView rootView = new RootView("Testing...", new Dimension(500, 500), v, false, false);
        rootView.showFrame();
    }

}
