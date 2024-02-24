package com.realssoft.dulcefrio.ui.page.menu;

import net.miginfocom.swing.MigLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

public class ItemsManager extends JPanel
{

    private static final ItemsManager INSTANCE = new ItemsManager();

    private ItemsManager()
    {
        super();
        setBorder(null);
        configureProperties();
    }

    public static ItemsManager getInstance()
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
                "10[100%,fill]10",
                "10[100%,fill]10")
        );
    }

}
