package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Getter;
import lombok.Setter;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;

public class MenuItem extends JPanel {

    @Setter
    private MenuEvent menuEvent;
    private final JPanel panelMenu;
    private final String title;
    @Getter
    private final int menuIndex;
    private final int menuItemHeight = 40;
    private final int subMenuItemHeight = 35;
    private final int subMenuLeftGap = 34;
    private final int firstGap = 5;
    private final int bottomGap = 5;
    private float animate;

    public MenuItem(JPanel panelMenu,String path, String title, int menuIndex)
    {
        this.panelMenu = panelMenu;
        this.title = title;
        this.menuIndex = menuIndex;
        configureProperties();
        configureComponents(path);
    }

    private void configureProperties()
    {
        setLayout(new MenuItemLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.background;"
                + "foreground:$Menu.lineColor");

    }

    private void configureComponents(String path)
    {
        JButton menuItem = createButtonItem(title);
        menuItem.setIcon(SVGUtils.getSVGIcon(path));
        menuItem.setHorizontalAlignment(
                menuItem.getComponentOrientation().isLeftToRight()
                        ? SwingConstants.LEADING
                        : SwingConstants.TRAILING
        );
        menuItem.setVerticalAlignment(SwingConstants.CENTER);
        menuItem.addActionListener(e -> {
            SVGUtils.setSelectedIndex(panelMenu, menuIndex);
            menuEvent.action();
        });
        add(menuItem);
    }

    private JButton createButtonItem(String text)
    {
        JButton button = new JButton(text);
        button.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.background;"
                + "selectedBackground:$Component.accentColor;" +
                "selectedForeground:#fff;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "arc:999;"
                + "iconTextGap:10;"
                + "margin:0,11,0,11");
        return button;
    }

    public void setFull(boolean full) {
        if (full) {
            int size = getComponentCount();
            for (int i = 0; i < size; i++) {
                Component com = getComponent(i);
                if (com instanceof JButton) {
                    JButton button = (JButton) com;
                    button.setText(title);
                    button.setHorizontalAlignment(
                            getComponentOrientation().isLeftToRight()
                                    ? JButton.LEFT
                                    : JButton.RIGHT
                    );
                }
            }
        } else {
            for (Component com : getComponents()) {
                if (com instanceof JButton) {
                    JButton button = (JButton) com;;
                    button.setText("");
                    button.setHorizontalAlignment(JButton.CENTER);
                }
            }
            animate = 0f;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (animate > 0) {
            int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
            int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
            int smenuItemHeight = UIScale.scale(menuItemHeight);
            int sfirstGap = UIScale.scale(firstGap);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D.Double p = new Path2D.Double();
            int last = getComponent(getComponentCount() - 1).getY() + (ssubMenuItemHeight / 2);
            boolean ltr = getComponentOrientation().isLeftToRight();
            int round = UIScale.scale(10);
            int x = ltr ? (ssubMenuLeftGap - round) : (getWidth() - (ssubMenuLeftGap - round));
            p.moveTo(x, smenuItemHeight + sfirstGap);
            p.lineTo(x, last - round);
            for (int i = 1; i < getComponentCount(); i++) {
                int com = getComponent(i).getY() + (ssubMenuItemHeight / 2);
                p.append(createCurve(round, x, com, ltr), false);
            }
            g2.setColor(getForeground());
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setStroke(new BasicStroke(UIScale.scale(1f)));
            g2.draw(p);
            g2.dispose();
        }
    }

    private Shape createCurve(int round, int x, int y, boolean ltr) {
        Path2D p2 = new Path2D.Double();
        p2.moveTo(x, y - round);
        p2.curveTo(x, y - round, x, y, x + (ltr ? round : -round), y);
        return p2;
    }

    private class MenuItemLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets inset = parent.getInsets();
                int width = parent.getWidth();
                int height = inset.top + inset.bottom;
                int size = parent.getComponentCount();
                Component item = parent.getComponent(0);
                height += UIScale.scale(menuItemHeight) + 2;
                if (item.isVisible()) {
                    int subMenuHeight = size > 1 ? UIScale.scale(firstGap) + UIScale.scale(bottomGap) : 0;
                    for (int i = 1; i < size; i++) {
                        Component com = parent.getComponent(i);
                        if (com.isVisible()) {
                            subMenuHeight += UIScale.scale(subMenuItemHeight);
                        }
                    }
                    height += (subMenuHeight * animate);
                } else {
                    height = 0;
                }
                return new Dimension(width, height);
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
                Insets insets = parent.getInsets();
                int x = insets.left ;
                int y = insets.top ;
                int width = parent.getWidth() - (insets.left + insets.right);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        if (i == 0) {
                            int smenuItemHeight = UIScale.scale(menuItemHeight);
                            int sfirstGap = UIScale.scale(firstGap);
                            com.setBounds(x, y, width, smenuItemHeight);
                            y += smenuItemHeight + sfirstGap;
                        } else {
                            int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                            int subMenuX = ltr ? ssubMenuLeftGap : 0;
                            int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
                            com.setBounds(x + subMenuX, y, width - ssubMenuLeftGap, ssubMenuItemHeight);
                            y += ssubMenuItemHeight;
                        }
                    }
                }
            }
        }
    }
}
