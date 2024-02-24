package com.realssoft.dulcefrio.ui.page;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.ui.components.Menu;
import com.realssoft.dulcefrio.ui.components.ShadowPanel;
import com.realssoft.dulcefrio.ui.page.menu.ItemsManager;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

public class MenuPage extends JLayeredPane
{

    private static final MenuPage INSTANCE = new MenuPage();
    private Menu menu;
    private ShadowPanel shadowPanel;
    private JPanel panelBody;
    private JButton menuButton;

    public MenuPage()
    {
        configureProperties();
        configureComponents();
    }

    public static MenuPage getInstance()
    {
        return INSTANCE;
    }

    private void configureProperties()
    {

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        panelBody = ItemsManager.getInstance();
        initMenuArrowIcon();
        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Component.accentColor;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(panelBody);
    }

    private void configureComponents()
    {
        menu = new Menu();
        add(menu);

        shadowPanel = new ShadowPanel();
        shadowPanel.setArc(10);
        add(shadowPanel);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? PathRS.SVG_MENU_LEFT : PathRS.SVG_MENU_RIGHT;
        menuButton.setIcon(new FlatSVGIcon(icon, 0.9f));
    }

    public void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? PathRS.SVG_MENU_LEFT : PathRS.SVG_MENU_RIGHT;
        } else {
            icon = (full) ? PathRS.SVG_MENU_RIGHT : PathRS.SVG_MENU_LEFT;
        }
        menuButton.setIcon(new FlatSVGIcon(icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                shadowPanel.setBounds(menuX , y, menuWidth +4 , height);
                menu.setBounds(menuX + 5, y + 3, menuWidth - 4, height - 8);

                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + (menuWidth + 7) - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) ((menuX + 5) - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                menuButton.setBounds(menubX - 5, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx + 12, bodyy - 13, bodyWidth - 15, bodyHeight + 17);
            }
        }
    }

}
