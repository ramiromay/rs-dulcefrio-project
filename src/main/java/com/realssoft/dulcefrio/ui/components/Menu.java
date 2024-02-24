package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.Main;
import com.realssoft.dulcefrio.api.config.PropertiesConfiguration;
import com.realssoft.dulcefrio.api.persistence.dao.impl.AccountDaoImpl;
import com.realssoft.dulcefrio.ui.dialog.FrameInstance;
import com.realssoft.dulcefrio.ui.page.LoginPage;
import com.realssoft.dulcefrio.ui.page.MenuPage;
import com.realssoft.dulcefrio.ui.page.PageManager;
import com.realssoft.dulcefrio.ui.page.menu.CashRegisterItem;
import com.realssoft.dulcefrio.ui.page.menu.EmployeeItem;
import com.realssoft.dulcefrio.ui.page.menu.ItemsManager;
import com.realssoft.dulcefrio.ui.page.menu.ProductItem;
import com.realssoft.dulcefrio.ui.page.menu.SaleItem;
import com.realssoft.dulcefrio.ui.page.menu.ShoppingCartItem;
import com.realssoft.dulcefrio.ui.page.menu.UserItem;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.rs.StringRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class Menu extends JPanel {

    private final ItemsManager itemsManager = ItemsManager.getInstance();

    private MenuItem productItem;
    private MenuItem employeeItem;
    private MenuItem userItem;
    private MenuItem saleItem;
    private MenuItem cartItem;
    private MenuItem boxCutItem;
    private MenuItem logoutItem;

    private boolean menuFull = true;
    protected final boolean hideMenuTitleOnMinimum = true;
    protected final int menuTitleLeftInset = 5;
    protected final int menuTitleVgap = 5;
    protected final int menuMaxWidth = 250;
    protected final int menuMinWidth = 60;
    protected final int headerFullHgap = 5;
    private final boolean admin = Main.ADMIN;
    private int itemSelected = 0;

    public Menu()
    {
        configureProperties();
        configureComponents();
        configureListener();
    }

    private void configureProperties()
    {
        setOpaque(false);
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:20,2,2,2;"
                + "background:$Menu.background;"
                + "arc:10");
    }

    private void configureComponents()
    {
        ImageIcon logo = (ImageIcon) SVGUtils.getSVGIcon(PathRS.SVG_MICHOACANA, 35, 35);
        header = new JLabel(StringRS.NAME_APP);
        header.setIcon(logo);
        header.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.header.font;" +
                "iconTextGap:10;");
        add(header);

        scroll = new JScrollPane();
        panelMenu = new JPanel(new MenuOptionsLayout(this));
        panelMenu.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5;"
                + "background:$Menu.background");
        scroll.setViewportView(panelMenu);
        scroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:null");
        add(scroll);

        JScrollBar vscroll = scroll.getVerticalScrollBar();
        vscroll.setUnitIncrement(10);
        vscroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "width:$Menu.scroll.width;"
                + "trackInsets:$Menu.scroll.trackInsets;"
                + "thumbInsets:$Menu.scroll.thumbInsets;"
                + "background:$background.shade.black"
                + "thumb:$Menu.ScrollBar.thumb");
        createMenu();

        themeMode = new ThemeMode();
        themeMode.setVisible(false);

        SVGUtils.setSelectedIndex(panelMenu, 0);
        ProductItem item = ProductItem.getInstance();
        itemsManager.showActivity(item);
    }

    private JLabel createTitle(String title)
    {
        JLabel lbTitle = new JLabel(title);
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.label.font;"
                + "foreground:$gray");
        return lbTitle;
    }

    private void createMenu()
    {

        JLabel lbMenu = createTitle("MENU");
        panelMenu.add(lbMenu);

        productItem = new MenuItem(
                panelMenu,
                PathRS.SVG_MENU_ITEM.formatted(1),
                "Productos",
                0
        );
        panelMenu.add(productItem);

        if (admin)
        {
            employeeItem = new MenuItem(
                    panelMenu,
                    PathRS.SVG_MENU_ITEM.formatted(2),
                    "Empleados",
                    1
            );
            panelMenu.add(employeeItem);

            userItem = new MenuItem(
                    panelMenu,
                    PathRS.SVG_MENU_ITEM.formatted(3),
                    "Usuarios",
                    2);
            panelMenu.add(userItem);

            saleItem = new MenuItem(
                    panelMenu,
                    PathRS.SVG_MENU_ITEM.formatted(4),
                    "Ventas",
                    3
            );
            panelMenu.add(saleItem);
        }

        cartItem = new MenuItem(
                panelMenu,
                PathRS.SVG_MENU_ITEM.formatted(5),
                "Carrito",
                4
        );
        panelMenu.add(cartItem);

        boxCutItem = new MenuItem(
                panelMenu,
                PathRS.SVG_MENU_ITEM.formatted(6),
                "Caja Registradora",
                5
        );
        panelMenu.add(boxCutItem);

        JLabel lbOther = createTitle("OTROS");
        panelMenu.add(lbOther);

        logoutItem = new MenuItem(
                panelMenu,
                PathRS.SVG_MENU_ITEM.formatted(7),
                "Cerrar Sessión",
                6);
        panelMenu.add(logoutItem);

        productItem.setToolTipText("Productos");
        if (admin)
        {
            employeeItem.setToolTipText("Empleados");
            userItem.setToolTipText("Usuarios");
            saleItem.setToolTipText("Ventas");
        }
        cartItem.setToolTipText("Carrito");
        boxCutItem.setToolTipText("Caja Registradora");
        logoutItem.setToolTipText("Cerrar Sesión");
    }

    private void configureListener()
    {
        productItem.setMenuEvent(()-> {
            ProductItem item = ProductItem.getInstance();
            itemsManager.showActivity(item);
            itemSelected =  0;
        });
        if (Main.ADMIN)
        {
            employeeItem.setMenuEvent(()-> {
                EmployeeItem item = EmployeeItem.getInstance();
                itemsManager.showActivity(item);
                itemSelected =  1;
            });
            userItem.setMenuEvent(()-> {
                UserItem item = UserItem.getInstance();
                itemsManager.showActivity(item);
                itemSelected =  2;
            });
            saleItem.setMenuEvent(()-> {
                SaleItem item = SaleItem.getInstance();
                itemsManager.showActivity(item);
                itemSelected =  3;
            });
        }
        cartItem.setMenuEvent(()-> {
            ShoppingCartItem item = ShoppingCartItem.getInstance();
            itemsManager.showActivity(item);
            itemSelected = 4;
        });
        boxCutItem.setMenuEvent(()-> {
            CashRegisterItem  item = CashRegisterItem.getInstance();
            itemsManager.showActivity(item);
            itemSelected = 5;
        });
        logoutItem.setMenuEvent(()-> {

            int option = JOptionPane.showConfirmDialog(
                    FrameInstance.getInstance(),
                    "¿Desea cerrar sesión?",
                    "Confirmar Cierre de Sesión",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (option == JOptionPane.OK_OPTION)
            {
                SVGUtils.setSelectedIndex(panelMenu, 0);
                itemsManager.showActivity(ProductItem.getInstance());

                ProductItem.getInstance().setFirstBoot(true);
                EmployeeItem.getInstance().setFirstBoot(true);
                UserItem.getInstance().setFirstBoot(true);
                SaleItem.getInstance().setFirstBoot(true);
                ShoppingCartItem.getInstance().setFirstBoot(true);
                CashRegisterItem.getInstance().setFirstBoot(true);

                PropertiesConfiguration.getInstance().setToken("");
                PropertiesConfiguration.getInstance().setUserId("");

                //cerrarSesion
                Main.ADMIN = false;
                AccountDaoImpl.getInstance().logout(Main.ID_USER);
                PageManager.getInstance().showActivity(new LoginPage());
                MenuPage.getInstance().setMenuFull(true);
                return;
            }

            SVGUtils.setSelectedIndex(panelMenu, itemSelected);

        });
    }

    public boolean isMenuFull()
    {
        return menuFull;
    }

    public void setMenuFull(boolean menuFull)
    {
        this.menuFull = menuFull;
        if (menuFull) {
            header.setText(StringRS.NAME_APP);
            header.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
        } else {
            header.setText("");
            header.setHorizontalAlignment(JLabel.CENTER);
        }
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).setFull(menuFull);
            }
        }
        themeMode.setMenuFull(menuFull);
    }

    public boolean isHideMenuTitleOnMinimum() {
        return hideMenuTitleOnMinimum;
    }

    public int getMenuTitleLeftInset() {
        return menuTitleLeftInset;
    }

    public int getMenuTitleVgap() {
        return menuTitleVgap;
    }

    public int getMenuMaxWidth() {
        return menuMaxWidth;
    }

    public int getMenuMinWidth() {
        return menuMinWidth;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
        g2.dispose();
        super.paintComponent(g);
    }

    private JLabel header;
    private JScrollPane scroll;
    private JPanel panelMenu;
    private ThemeMode themeMode;

    private class MenuLayout implements LayoutManager {

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
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int gap = UIScale.scale(5);
                int sheaderFullHgap = UIScale.scale(headerFullHgap);
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int iconWidth = width;
                int iconHeight = header.getPreferredSize().height;
                int hgap = menuFull ? sheaderFullHgap : 0;
                int accentColorHeight = 0;

                header.setBounds(x + hgap, y, iconWidth - (hgap * 2), iconHeight);
                int ldgap = UIScale.scale(10);

                int menux = x;
                int menuy = y + iconHeight + gap;
                int menuWidth = width;
                int menuHeight = height - (iconHeight + gap) - (ldgap * 2) - (accentColorHeight) + 8;
                scroll.setBounds(menux, menuy, menuWidth, menuHeight);

            }
        }
    }
}
