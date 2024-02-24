package com.realssoft.dulcefrio.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ShoppingCartTitle extends ShadowPanel
{

    private JPanel mainPanel;
    private JLabel searchButton;
    private JTextField  searchField;
    private JComboBox<String> searchOptions;

    @Setter
    private int arc = UIScale.scale(55);
    private final Color backgroundBar = UIManager.getColor("SearchBar");
    private final Color backgroundFilter = UIManager.getColor("SearchBar.filter");
    private final Color backgroundShadow = UIManager.getColor("background.shade");
    private final Color background = UIManager.getColor("@background");

    public ShoppingCartTitle()
    {
        super(0.0f);
        configureProperties();
        configureComponents();
        configureLayout();
    }

    private void configureProperties()
    {
        setOpaque(false);
        setBackground(backgroundBar);
        setLayout(new MigLayout(
                "fillx", "3[fill]3", "3[fill]3")
        );
        setArc(UIScale.scale(50));
        revalidate();
        repaint();
    }

    private void configureComponents()
    {
        mainPanel = new RoundPanel();
        mainPanel.setBorder(new EmptyBorder(4, 7, 4, 7));
        mainPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;"
        );
        add(mainPanel);

        searchButton = new JLabel();
        searchButton.setHorizontalAlignment(SwingConstants.CENTER);
        searchButton.setVerticalAlignment(SwingConstants.CENTER);
        searchButton.setIcon(SVGUtils.getSVGIcon("svg/employee-fill.svg"));
        searchButton.putClientProperty(FlatClientProperties.STYLE, "" +
                "background :$SearchBar;" +
                "foreground:@foreground.white;"
        );
        mainPanel.add(searchButton);

        searchField = new JTextField();
        searchField.setEnabled(false);
        searchField.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$SearchBar;" +
                "foreground:$blackColor-2;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:999;" +
                "font:15;"
        );
        mainPanel.add(searchField);

        searchOptions = new JComboBox<>();
        searchOptions.setEnabled(false);
        searchOptions.putClientProperty(FlatClientProperties.STYLE, "" +
                "disabledBackground:$SearchBar.filter;" +
                "buttonDisabledArrowColor:$SearchBar.filter;" +
                "disabledForeground:#333;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "foreground:#000;" +
                "arc:25;" +
                "font:13;"
        );
        mainPanel.add(searchOptions);

    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(searchButton,  GroupLayout.DEFAULT_SIZE,
                        38, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE,
                        22, Short.MAX_VALUE)
                .addComponent(searchOptions, GroupLayout.DEFAULT_SIZE,
                        150, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(searchButton, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
                .addComponent(searchField, GroupLayout.DEFAULT_SIZE,
                        20, Short.MAX_VALUE)
                .addComponent(searchOptions, GroupLayout.DEFAULT_SIZE,
                        30, GroupLayout.PREFERRED_SIZE)
        );
    }

    public void setHoverText(String name, String role)
    {
        searchOptions.removeAllItems();
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, name);
        searchOptions.addItem(role);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));
        g2.dispose();
        super.paintComponent(g);
    }

}
