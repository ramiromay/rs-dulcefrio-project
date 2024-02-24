package com.realssoft.dulcefrio.ui.page;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class PageManager extends JPanel
{

    private static final PageManager INSTANCE = new PageManager();

    private PageManager()
    {
        super();
        setBorder(null);
        configureProperties();
    }

    public static PageManager getInstance()
    {
        return INSTANCE;
    }

    public void showActivity(Component activity)
    {
        removeAll();
        add(activity);
        repaint();
        revalidate();
    }

    public boolean isAncestor(Component c)
    {
        Container p;
        if (c == null || ((p = c.getParent()) == null))
        {
            return false;
        }
        while (p != null)
        {
            if (p == this)
            {
                return true;
            }
            p = p.getParent();
        }
        return false;
    }

    private void configureProperties()
    {
        setLayout(new MigLayout(
                "fill",
                "0[100%,fill]0",
                "0[100%,fill]0")
        );
    }
}
