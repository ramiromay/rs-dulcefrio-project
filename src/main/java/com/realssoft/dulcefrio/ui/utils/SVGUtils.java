package com.realssoft.dulcefrio.ui.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.realssoft.dulcefrio.ui.components.MenuItem;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SVGUtils
{

    public static @NotNull Icon getSVGIcon(String path, int width, int height)
    {
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon(path, width, height);
        FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
        colorFilter.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(colorFilter);
        return icon;
    }

    public static @NotNull Icon getSVGIcon(String path)
    {
        Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
        Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
        FlatSVGIcon icon = new FlatSVGIcon(path);
        FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
        colorFilter.add(Color.decode("#969696"), lightColor, darkColor);
        icon.setColorFilter(colorFilter);
        return icon;
    }

    public static void setSelectedIndex(@NotNull JPanel panelMenu, int menuIndex)
    {
        int size = panelMenu.getComponentCount();
        for (int i = 0; i < size; i++)
        {
            Component com = panelMenu.getComponent(i);
            if (com instanceof MenuItem)
            {
                MenuItem menuItem = (MenuItem) com;
                JButton button = (JButton) menuItem.getComponent(0);

                if (button.isSelected())
                {
                    button.setSelected(false);
                    button.setIcon(getSVGIcon(PathRS.SVG_MENU_ITEM.formatted(menuItem.getMenuIndex() + 1)));
                }
                if (menuItem.getMenuIndex() == menuIndex)
                {
                    button.setSelected(true);
                    button.setIcon(getSVGIcon(PathRS.SVG_MENU_ITEM_FILL.formatted(menuIndex + 1)));
                }

            }

        }
    }

}
