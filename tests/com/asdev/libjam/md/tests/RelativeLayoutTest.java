package com.asdev.libjam.md.tests;

import com.asdev.libjam.md.base.RootView;
import com.asdev.libjam.md.drawable.ColorDrawable;
import com.asdev.libjam.md.layout.GenericLayoutParamList;
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
        GenericLayoutParamList list = new GenericLayoutParamList();
        list.putParam("gravity", 0);
        child.applyLayoutParameters(list);

        child.setBackground(new ColorDrawable(Color.BLACK));

        View child2 = new View();
        GenericLayoutParamList list2 = new GenericLayoutParamList();
        list2.putParam("gravity", 1);
        child2.applyLayoutParameters(list2);

        child2.setBackground(new ColorDrawable(Color.BLUE));

        View child3 = new View();
        GenericLayoutParamList list3 = new GenericLayoutParamList();
        list3.putParam("gravity", 2);
        child3.applyLayoutParameters(list3);

        child3.setBackground(new ColorDrawable(Color.RED));

        View child4 = new View();
        GenericLayoutParamList list4 = new GenericLayoutParamList();
        list4.putParam("gravity", 3);
        child4.applyLayoutParameters(list4);

        child4.setBackground(new ColorDrawable(Color.GREEN));

        View child5 = new View();
        GenericLayoutParamList list5 = new GenericLayoutParamList();
        list5.putParam("gravity", 5);
        child5.applyLayoutParameters(list5);

        child5.setBackground(new ColorDrawable(Color.YELLOW));

        View child6 = new View();
        GenericLayoutParamList list6 = new GenericLayoutParamList();
        list6.putParam("gravity", 6);
        child6.applyLayoutParameters(list6);

        child6.setBackground(new ColorDrawable(Color.CYAN));

        RelativeLayout l2 = new RelativeLayout();
        GenericLayoutParamList l = new GenericLayoutParamList();
        l.putParam("gravity", 8);
        l2.applyLayoutParameters(l);
        l2.setBackground(new ColorDrawable(Color.DARK_GRAY));

        View child7 = new View();
        GenericLayoutParamList list7 = new GenericLayoutParamList();
        list7.putParam("gravity", 0);
        child7.applyLayoutParameters(list7);

        child7.setBackground(new ColorDrawable(Color.MAGENTA));

        View child8 = new View();
        GenericLayoutParamList list8 = new GenericLayoutParamList();
        list8.putParam("gravity", 8);
        child8.applyLayoutParameters(list8);

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
